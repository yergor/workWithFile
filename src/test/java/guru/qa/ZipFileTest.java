
package guru.qa;


import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import guru.qa.domain.TestDescription;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import static org.assertj.core.api.Assertions.assertThat;

public class ZipFileTest {
    ClassLoader classLoader = ZipFileTest.class.getClassLoader();
    String zipName = "test.zip";
    String zipPath = "src/test/resources/";
    String xlsFileName = "excel.xls";
    String pdfFileName = "pdf.pdf";
    String csvFileName = "csv.csv";

    @Test
    void testZipCsv() throws Exception {
        InputStream is = classLoader.getResourceAsStream(zipName);
        ZipInputStream zis = new ZipInputStream(is);
        ZipFile zfile = new ZipFile(new File(zipPath + zipName));
        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null) {
            if (entry.getName().equals(csvFileName)) {
                try (InputStream stream = zfile.getInputStream(entry);
                     CSVReader reader = new CSVReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                    List<String[]> csv = reader.readAll();
                    assertThat(csv).contains(
                            new String[]{"year", "industry_code_ANZSIC", "industry_name_ANZSIC", "rme_size_grp", "variable", "value", "unit"}
                    );
                }
            }
        }
        if (is != null) {
            is.close();
            zis.close();
        }
    }

    @Test
    void testZipPdf() throws Exception {
        InputStream is = classLoader.getResourceAsStream(zipName);
        ZipInputStream zis = new ZipInputStream(is);
        ZipFile zfile = new ZipFile(new File(zipPath + zipName));
        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null) {
            if (entry.getName().equals(pdfFileName)) {
                try (InputStream stream = zfile.getInputStream(entry)) {
                    PDF pdf = new PDF(stream);
                    assertThat(pdf.text).contains("Редактор:");
                }
            }

        }
        if (is != null) {
            is.close();
            zis.close();
        }
    }

    @Test
    void testZipXls() throws Exception {
        InputStream is = classLoader.getResourceAsStream(zipName);
        ZipInputStream zis = new ZipInputStream(is);
        ZipFile zfile = new ZipFile(new File(zipPath + zipName));
        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null) {
            if (entry.getName().equals(xlsFileName)) {
                try (InputStream stream = zfile.getInputStream(entry)) {
                    XLS xls = new XLS(stream);
                    assertThat(
                            xls.excel.getSheetAt(0)
                                    .getRow(0)
                                    .getCell(1)
                                    .getStringCellValue()
                    ).contains("Тест кейс");
                }
            }

        }
        if (is != null) {
            is.close();
            zis.close();
        }
    }

    @Test
    void jsonTest() throws IOException {
        InputStream is = classLoader.getResourceAsStream("test.json");
        ObjectMapper objectMapper = new ObjectMapper();
        TestDescription test = objectMapper.readValue(is, TestDescription.class);
        assertThat(test.getName()).isEqualTo("Ed");
    }
        }
