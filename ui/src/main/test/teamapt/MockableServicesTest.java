package teamapt;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.*;
import com.itextpdf.tool.xml.exceptions.RuntimeWorkerException;
import com.itextpdf.tool.xml.html.CssAppliersImpl;
import com.itextpdf.tool.xml.html.HTML;
import com.itextpdf.tool.xml.html.TagProcessorFactory;
import com.itextpdf.tool.xml.html.Tags;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;
import com.itextpdf.xmp.impl.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import teamapt.application.ui.config.SandBoxConfig;
import teamapt.application.ui.domains.Employee;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

import static org.junit.Assert.*;


@RunWith(MockitoJUnitRunner.class)
public class MockableServicesTest {

    @Mock
    ArrayList<String> arrayList = new ArrayList<>();
    @Mock
    ArrayList<Object> mockedList = new ArrayList<>();
    @Spy
    List<String> spiedList = new ArrayList<String>();
    @Captor
    ArgumentCaptor argCaptor;

    @Test
    public void testMockList() {
        Mockito.when(arrayList.size()).thenReturn(200);
        assertEquals(arrayList.size(), 0);
    }

    @Test
    public void whenUseSpyAnnotation_thenSpyIsInjected() {
        spiedList.add("one");
        spiedList.add("two");
        spiedList.remove("three");


        Mockito.verify(spiedList).add("one");
        Mockito.verify(spiedList).add("two");
        Mockito.verify(spiedList).remove("three");

        System.out.printf("Size of spied is %d ", spiedList.size());

        Mockito.doReturn(100).when(spiedList).size();
        assert (100 == spiedList.size());
    }

    @Test
    public void testDateTime() {
        System.out.println(LocalDate.now());
    }

    @Test
    public void testExcelWriter() {
        ExcelWriterMock writerMock = new ExcelWriterMock();
        try {
            writerMock.testCreateExcel();
        } catch (Exception exp) {

        }
    }

    @Test
    public void whenUseCaptorAnnotation_thenTheSam() {
        mockedList.add("one");
        Mockito.verify(mockedList).add(argCaptor.capture());
        System.out.println(argCaptor.getAllValues());
    }


    @Test
    public void dateFormatter(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a", Locale.ENGLISH);
        LocalDateTime startTime = LocalDateTime.parse("01/01/2019 00:00 AM",formatter);
        System.out.println(startTime);
    }


    @Test
    public void generate_with_image() throws URISyntaxException, DocumentException, IOException{
        Path path = Paths.get(ClassLoader.getSystemResource("logo.png").toURI());

        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream("test.pdf"));
        document.open();

        Image img = Image.getInstance(path.toAbsolutePath().toString());
        document.add(img);

        document.close();
    }


    @Test
    public void testConfig(){
        SandBoxConfig config = new SandBoxConfig();
        config.fileFinder();
    }
    @Test
    public void generate_1() throws IOException, DocumentException, URISyntaxException {

        final String PLACEHOLDER = "[[CONTENT]]";

        FileInputStream fileInputStream = new FileInputStream("pos_template.html");
        String content = IOUtils.toString(fileInputStream, Charset.defaultCharset());

        if (StringUtils.isEmpty(content)) {
            System.out.println("No content found in pos receipt template.");
        }




        content = content.replace(PLACEHOLDER, "Creating a pdf with a use of the iText library is based on manipulating objects implementing Elements interface in Document (in version 5.5.10 there are 45 of those implementations).\n" +
                "\n" +
                "The smallest element which can be added to the document and used is called Chunk, which is basically a string with applied font.\n" +
                "\n" +
                "Additionally, Chunk‘s can be combined with other elements like Paragraphs, Section etc. resulting in nice looking documents.");

        content = content.replace("[[LOGO]]",
                Paths.get(new File("./receipts/logo.png").toURI()).toAbsolutePath().toString());



        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(80);

        OutputStream file = new FileOutputStream(new File("pos_template.pdf"));
        Document document = new Document();
        ElementList list = XMLWorkerHelper.parseToElementList(content, null);
        PdfWriter.getInstance(document, file);
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        document.open();
        for (Element element : list) {
            cell.addElement(element);
        }
        table.addCell(cell);
        document.add(table);
        document.close();


    }



    @Test
    public void generate_2() throws IOException, DocumentException {

        final String PLACEHOLDER = "[[CONTENT]]";

        FileInputStream fileInputStream = new FileInputStream("pos_template.html");
        String content = IOUtils.toString(fileInputStream, Charset.defaultCharset());

        if (StringUtils.isEmpty(content)) {
            System.out.println("No content found in pos receipt template.");
        }


        final TagProcessorFactory tagProcessorFactory = Tags.getHtmlTagProcessorFactory();
        tagProcessorFactory.removeProcessor(HTML.Tag.IMG);
        tagProcessorFactory.addProcessor(new ImageTagProcessor(), HTML.Tag.IMG);

        content = content.replace(PLACEHOLDER, "Maison Armani");

        Document document = new Document(PageSize.A4, 9f, 15f, 125f, 5f);

        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(80);


        final HtmlPipelineContext hpc = new HtmlPipelineContext(new CssAppliersImpl(new XMLWorkerFontProvider()));
        hpc.setAcceptUnknown(true).autoBookmark(true).setTagFactory(tagProcessorFactory);


        OutputStream file = new FileOutputStream(new File("pos_template.pdf"));
        ElementList list = XMLWorkerHelper.parseToElementList(content, null);
        PdfWriter.getInstance(document, file);
        PdfPCell cell = new PdfPCell();
        document.open();
        for (Element element : list) {
            cell.addElement(element);
        }
        table.addCell(cell);
        document.add(table);
        document.close();
    }


    @Test
    public void getExcelPOI() {
        File file = null;
        FileOutputStream fos = null;
        XSSFWorkbook workbook = null;
        XSSFSheet sheet = null;
        XSSFRow row = null;
        XSSFCell cell = null;
        try {
            workbook = new XSSFWorkbook();
            sheet = workbook.createSheet("Merge Test");

            row = sheet.createRow(0);
            cell = row.createCell(0);
            cell.setCellValue("Region One.");
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));

            row = sheet.createRow(1);

            cell = row.createCell(0);
            cell.setCellValue("Region Two.");
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 4));

            file = new File("Fever.xlsx");
            fos = new FileOutputStream(file);
            workbook.write(fos);
        } catch (Exception ex) {
            System.out.println("Caught an: " + ex.getClass().getName());
            System.out.println("Message: " + ex.getMessage());
            System.out.println("Stacktrace follows............");
            ex.printStackTrace(System.out);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ioEx) {}
            }
        }
    }
}


class ImageTagProcessor extends com.itextpdf.tool.xml.html.Image {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public List<Element> end(final WorkerContext ctx, final Tag tag, final List<Element> currentContent) {
        final Map<String, String> attributes = tag.getAttributes();
        String src = attributes.get(HTML.Attribute.SRC);
        List<Element> elements = new ArrayList<Element>(1);
        if (null != src && src.length() > 0) {
            Image img = null;
            if (src.startsWith("data:image/")) {
                final String base64Data = src.substring(src.indexOf(",") + 1);
                try {
                    img = Image.getInstance(Base64.decode(base64Data));
                } catch (Exception e) {
                    System.out.println("Exception occured");
                }
                if (img != null) {
                    try {
                        final HtmlPipelineContext htmlPipelineContext = getHtmlPipelineContext(ctx);
                        elements.add(getCssAppliers().apply(new Chunk((com.itextpdf.text.Image) getCssAppliers().apply(img, tag, htmlPipelineContext), 0, 0, true), tag,
                                htmlPipelineContext));
                    } catch (NoCustomContextException e) {
                        throw new RuntimeWorkerException(e);
                    }
                }
            }

            if (img == null) {
                elements = super.end(ctx, tag, currentContent);
            }
        }
        return elements;
    }
}

class ExcelWriterMock {

    private static String[] columns = {"Name", "Email", "Date Of Birth", "Salary"};
    private static List<Employee> employees = new ArrayList<>();

    // Initializing employees data to insert into the excel file
    static {
        Calendar dateOfBirth = Calendar.getInstance();
        dateOfBirth.set(1992, 7, 21);
        employees.add(new Employee("Rajeev Singh", "rajeev@example.com",
                dateOfBirth.getTime(), 1200000.0));
    }

    public void testCreateExcel() throws IOException, InvalidFormatException {
        // Create a Workbook
        Workbook workbook = new XSSFWorkbook(); // new HSSFWorkbook() for generating `.xls` file

        /* CreationHelper helps us create instances of various things like DataFormat,
           Hyperlink, RichTextString etc, in a format (HSSF, XSSF) independent way */
        CreationHelper createHelper = workbook.getCreationHelper();

        // Create a Sheet
        Sheet sheet = workbook.createSheet("Employee");

        // Create a Font for styling header cells
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14);
        headerFont.setColor(IndexedColors.RED.getIndex());

        // Create a CellStyle with the font
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        // Create a Row
        Row headerRow = sheet.createRow(0);

        // Create cells
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerCellStyle);
        }

        // Create Cell Style for formatting Date
        CellStyle dateCellStyle = workbook.createCellStyle();
        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-yyyy"));

        // Create Other rows and cells with employees data
        int rowNum = 1;
        for (Employee employee : employees) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0)
                    .setCellValue(employee.getName());

            row.createCell(1)
                    .setCellValue(employee.getEmail());

            Cell dateOfBirthCell = row.createCell(2);
            dateOfBirthCell.setCellValue(employee.getDateOfBirth());
            dateOfBirthCell.setCellStyle(dateCellStyle);

            row.createCell(3)
                    .setCellValue(employee.getSalary());
        }

        // Resize all columns to fit the content size
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Write the output to a file
        FileOutputStream fileOut = new FileOutputStream("sync-ho.csv");
        workbook.write(fileOut);
        fileOut.close();

        // Closing the workbook
        workbook.close();
    }

    private static void modifyExistingWorkbook() throws InvalidFormatException, IOException {
        // Obtain a workbook from the excel file
        Workbook workbook = WorkbookFactory.create(new File("existing-spreadsheet.xlsx"));

        // Get Sheet at index 0
        Sheet sheet = workbook.getSheetAt(0);

        // Get Row at index 1
        Row row = sheet.getRow(1);

        // Get the Cell at index 2 from the above row
        Cell cell = row.getCell(2);

        // Create the cell if it doesn't exist
        if (cell == null)
            cell = row.createCell(2);

        // Update the cell's value
        cell.setCellType(CellType.STRING);
        cell.setCellValue("Updated Value");

        // Write the output to the file
        FileOutputStream fileOut = new FileOutputStream("existing-spreadsheet.xlsx");
        workbook.write(fileOut);
        fileOut.close();

        // Closing the workbook
        workbook.close();
    }
}


/***
 * Get the source and destination file.
 * Get the point where we need to replace the content
 * Go through destination src (key) and replace with destination content
 * Get column from key in source
 *
 * */


