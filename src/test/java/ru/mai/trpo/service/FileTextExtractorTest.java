package ru.mai.trpo.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.multipart.MultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class FileTextExtractorTest {

    @Mock
    private MultipartFile multipartFile;

    private FileTextExtractor fileTextExtractor;

    @BeforeEach
    void setUp() {
        fileTextExtractor = new FileTextExtractor();
    }

    @Test
    void testExtractTextFromTxt() throws IOException {
        String expectedText = "Hello, world!";
        Mockito.when(multipartFile.getContentType()).thenReturn("text/plain");
        Mockito.when(multipartFile.getBytes()).thenReturn(expectedText.getBytes(StandardCharsets.UTF_8));

        String result = fileTextExtractor.extractTextFromFile(multipartFile);

        assertThat(result).isEqualTo(expectedText);
    }

    @Test
    void testExtractTextFromPdf() throws IOException {
        // Путь к тестовому файлу
        var pdfResource = new ClassPathResource("files/file_with_text_for_test_extract_from_pdf.pdf");
        InputStream pdfInputStream = pdfResource.getInputStream();

        // Мокаем MultipartFile, чтобы возвращал контент тестового PDF
        Mockito.when(multipartFile.getContentType()).thenReturn("application/pdf");
        Mockito.when(multipartFile.getInputStream()).thenReturn(pdfInputStream);

        // Ожидаемый текст внутри файла, убедитесь, что он соответствует содержимому тестового PDF
        String expectedText = "Файл с текстом для модульного теста на извлечение из PDF.";

        // Вызываем метод
        String result = fileTextExtractor.extractTextFromFile(multipartFile);

        // Проверяем результат
        assertThat(result).contains(expectedText);
    }

    @Test
    void testExtractTextFromDocx() throws IOException {
        String paragraphText = "This is a DOCX file.";

        // Создадим DOCX документ в памяти
        XWPFDocument document = new XWPFDocument();
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.createRun().setText(paragraphText);

        // Сохраним документ в массив байт
        byte[] docxBytes;
        try (var baos = new java.io.ByteArrayOutputStream()) {
            document.write(baos);
            docxBytes = baos.toByteArray();
        }

        ByteArrayInputStream docxInputStream = new ByteArrayInputStream(docxBytes);

        Mockito.when(multipartFile.getContentType()).thenReturn("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        Mockito.when(multipartFile.getInputStream()).thenReturn(docxInputStream);

        String result = fileTextExtractor.extractTextFromFile(multipartFile);

        assertThat(result).contains(paragraphText);
    }

    @Test
    void testNullMimeType() throws IOException {
        Mockito.when(multipartFile.getContentType()).thenReturn(null);

        assertThatThrownBy(() -> fileTextExtractor.extractTextFromFile(multipartFile))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Не удалось определить MIME-тип файла");
    }

    @Test
    void testUnsupportedMimeType() throws IOException {
        Mockito.when(multipartFile.getContentType()).thenReturn("application/unknown");

        assertThatThrownBy(() -> fileTextExtractor.extractTextFromFile(multipartFile))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Неподдерживаемый MIME-тип файла");
    }

    @Test
    void testIOException() throws IOException {
        Mockito.when(multipartFile.getContentType()).thenReturn("text/plain");
        Mockito.when(multipartFile.getBytes()).thenThrow(new IOException("Test IO Exception"));

        assertThatThrownBy(() -> fileTextExtractor.extractTextFromFile(multipartFile))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ошибка при чтении файла");
    }
}
