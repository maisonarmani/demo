package teamapt.application.ui.services;

import org.apache.commons.net.ProtocolCommandEvent;
import org.apache.commons.net.ProtocolCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.commons.net.io.CopyStreamException;
import org.apache.commons.net.util.TrustManagerUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import teamapt.application.ui.config.ConfigProperties;

import javax.net.ssl.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Locale;

@Component

public class MockableService {

    private ConfigProperties properties;

    public MockableService() {
        properties  = new ConfigProperties();
        properties.setHost("196.46.20.7");
        properties.setPort(22);
        properties.setFileName("Stanbic IBTC PoS CPD 25 JAN 2017_SETT 25 JAN 2018.xlsx");
        properties.setFromFileDirectory("/SETTLEMENT REPORT");
        properties.setUsername("stanbic2");
        properties.setPassword("stb12@45");
        properties.setUseProxy(false);
    }

    public void letRunThrough() throws IOException {
        FTPSClient ftpsClient = getConnectedFTPSClient();

        ftpsClient.setRestartOffset(0);

        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();

        try {
            ftpsClient.retrieveFile(properties.getFromFileDirectory()+"/"+properties.getFileName(), byteOutputStream);
            byte[] downloadedBytes = byteOutputStream.toByteArray();

        } catch (CopyStreamException csEx) {
            // implement download re-initiation...
        } catch (IOException ioEx) {
            System.out.println(ioEx);
        }

    }


    private FTPSClient getConnectedFTPSClient() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                    }
            };

            SSLContext sslContext = SSLContext.getInstance("TLSv1");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            SSLContext.setDefault(sslContext);

            SSLSessionFTPSClient ftpsClient = new SSLSessionFTPSClient(false, sslContext);
            ftpsClient.setEnabledProtocols(new String[]{"TLSv1", "TLSv1.1", "TLSv1.2"});

            ftpsClient.setTrustManager(TrustManagerUtils.getAcceptAllTrustManager());

            ftpsClient.setHostnameVerifier(null);
            ftpsClient.setEndpointCheckingEnabled(false);
            setProxy(ftpsClient);

            ftpsClient.addProtocolCommandListener(new ProtocolCommandListener() {
                @Override
                public void protocolCommandSent(ProtocolCommandEvent event) {
                    System.out.println(String.format("Command: %s, Reply: %d, Message: %s", event.getCommand(), event.getReplyCode(), event.getMessage()));
                }

                @Override
                public void protocolReplyReceived(ProtocolCommandEvent event) {
                    System.out.println(String.format("Command: %s, Reply: %d, Message: %s", event.getCommand(), event.getReplyCode(), event.getMessage()));
                }
            });

            System.out.println(String.format("Connecting to Host %s on Port %d", properties.getHost(), properties.getPort()));

            ftpsClient.connect(properties.getHost(), properties.getPort());

            System.out.println("Returning connected instance");

            int reply = ftpsClient.getReplyCode();
            if (FTPReply.isPositiveCompletion(reply)) {
                // Login
                if (ftpsClient.login(properties.getUsername(), properties.getPassword())) {
                    System.out.println("FTP Login successful");

                    ftpsClient.execPBSZ(0);
                    ftpsClient.execPROT("P");
                    ftpsClient.enterLocalPassiveMode();
                    ftpsClient.setFileType(FTP.BINARY_FILE_TYPE);
                    ftpsClient.setBufferSize(1024000);
                    ftpsClient.setDataTimeout(0);
                    ftpsClient.setKeepAlive(true);
                } else {
                    System.out.println("FTP login failed");
                }
            }

            return ftpsClient;
        } catch (KeyManagementException | NoSuchAlgorithmException | IOException e) {
            System.out.println(e);
            System.out.println("Error occurred while creating connected FTPSClient instance");
        }

        return null;
    }


    private class SSLSessionFTPSClient extends FTPSClient {

        public SSLSessionFTPSClient() {
        }

        public SSLSessionFTPSClient(boolean implicit, SSLContext context) {
            super(implicit, context);
        }

        @Override
        protected void _prepareDataSocket_(final Socket socket) throws IOException {
            if(socket instanceof SSLSocket) {
                final SSLSession session = ((SSLSocket) _socket_).getSession();
                final SSLSessionContext context = session.getSessionContext();
                try {
                    final Field sessionHostPortCache = context.getClass().getDeclaredField("sessionHostPortCache");
                    sessionHostPortCache.setAccessible(true);
                    final Object cache = sessionHostPortCache.get(context);
                    final Method putMethod = cache.getClass().getDeclaredMethod("put", Object.class, Object.class);
                    putMethod.setAccessible(true);
                    final Method getHostMethod = socket.getClass().getDeclaredMethod("getHost");
                    getHostMethod.setAccessible(true);
                    Object host = getHostMethod.invoke(socket);
                    final String key = String.format("%s:%s", host, String.valueOf(socket.getPort())).toLowerCase(Locale.ROOT);
                    putMethod.invoke(cache, key, session);
                } catch(Exception e) {
                    System.out.println(e);
                }
            }
        }

        //@Override
        protected void _prepareDataSocket_2(final Socket socket) throws IOException {
            if (socket instanceof SSLSocket) {
                final SSLSession session = ((SSLSocket) _socket_).getSession();
                final SSLSessionContext context = session.getSessionContext();

                ((SSLSocket) socket).setEnabledProtocols(new String[]{"TLSv1", "TLSv1.1", "TLSv1.2"});
                try {
                    final Field sessionHostPortCache = context.getClass().getDeclaredField("sessionHostPortCache");
                    sessionHostPortCache.setAccessible(true);

                    final Object cache = sessionHostPortCache.get(context);
                    final Method putMethod = cache.getClass().getDeclaredMethod("put", Object.class, Object.class);
                    putMethod.setAccessible(true);

                    final Method getHostMethod = socket.getClass().getDeclaredMethod("getHost");
                    getHostMethod.setAccessible(true);

                    Object host = getHostMethod.invoke(socket);
                    final String key = String.format("%s:%s", host, String.valueOf(socket.getPort())).toLowerCase(Locale.ROOT);

                    putMethod.invoke(cache, key, session);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }

    }

    private void setProxy(SSLSessionFTPSClient ftpsClient) {
        if (ftpsClient == null || !properties.getUseProxy()) {
            return;
        }

        if (StringUtils.isEmpty(properties.getProxyHost()) || properties.getProxyPort() < 1) {
            return;
        }

        java.net.Proxy proxy = new java.net.Proxy(Proxy.Type.HTTP, new InetSocketAddress(properties.getProxyHost(), properties.getProxyPort()));
        ftpsClient.setProxy(proxy);
    }

}


