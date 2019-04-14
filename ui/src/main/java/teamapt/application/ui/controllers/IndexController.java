package teamapt.application.ui.controllers;


import com.jcraft.jsch.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ProtocolCommandEvent;
import org.apache.commons.net.ProtocolCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.commons.net.io.CopyStreamException;
import org.apache.commons.net.util.TrustManagerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import teamapt.application.ui.config.ConfigProperties;
import teamapt.application.ui.services.MockableService;

import javax.net.ssl.*;
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


import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Controller
public class IndexController {

    @Autowired
    private MockableService mockableService;

    @GetMapping("/")
    public boolean runFTPDownload() {
        try {
            mockableService.letRunThrough();
        }catch(Exception exp){
            return false;
        }

        return true;
    }
}

