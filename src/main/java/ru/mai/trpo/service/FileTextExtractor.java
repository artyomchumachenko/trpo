package ru.mai.trpo.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FileTextExtractor {

    /**
     * Извлечь текст из файла
     * @param file Файл с текстом
     * @return Строка с текстом
     */
    public String extractTextFromFile(MultipartFile file) {
        String mimeType = file.getContentType();

        try {
            if (mimeType == null) {
                throw new RuntimeException("Не удалось определить MIME-тип файла");
            }

            return switch (mimeType) {
                case "text/plain" -> extractTextFromTxt(file);
                case "application/pdf" -> extractTextFromPdf(file);
                case "application/vnd.openxmlformats-officedocument.wordprocessingml.document" ->
                        extractTextFromDocx(file);
                default -> throw new RuntimeException("Неподдерживаемый MIME-тип файла: " + mimeType);
            };
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при чтении файла", e);
        }
    }

    private String extractTextFromTxt(MultipartFile file) throws IOException {
        return new String(file.getBytes(), StandardCharsets.UTF_8);
    }

    private String extractTextFromPdf(MultipartFile file) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            return pdfStripper.getText(document);
        }
    }

    private String extractTextFromDocx(MultipartFile file) throws IOException {
        try (XWPFDocument doc = new XWPFDocument(file.getInputStream())) {
            return doc.getParagraphs().stream()
                    .map(XWPFParagraph::getText)
                    .reduce("", (acc, text) -> acc + "\n" + text);
        }
    }
}
