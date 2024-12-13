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

/**
 * Сервис для извлечения текста из файлов различных форматов.
 * <p>
 * Поддерживаемые форматы:
 * <ul>
 *     <li>text/plain (TXT)</li>
 *     <li>application/pdf (PDF)</li>
 *     <li>application/vnd.openxmlformats-officedocument.wordprocessingml.document (DOCX)</li>
 * </ul>
 */
@Service
@Slf4j
public class FileTextExtractor {

    /**
     * Извлекает текст из переданного файла.
     *
     * @param file файл, из которого необходимо извлечь текст
     * @return текстовое содержимое файла в виде строки
     * @throws RuntimeException если MIME-тип не определён или не поддерживается, либо при ошибках чтения файла
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

    /**
     * Извлекает текст из текстового файла (TXT).
     *
     * @param file TXT-файл
     * @return содержимое файла в виде строки
     * @throws IOException при ошибках чтения файла
     */
    private String extractTextFromTxt(MultipartFile file) throws IOException {
        return new String(file.getBytes(), StandardCharsets.UTF_8);
    }

    /**
     * Извлекает текст из PDF-файла.
     *
     * @param file PDF-файл
     * @return содержимое PDF в виде строки
     * @throws IOException при ошибках чтения файла
     */
    private String extractTextFromPdf(MultipartFile file) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            return pdfStripper.getText(document);
        }
    }

    /**
     * Извлекает текст из DOCX-файла.
     *
     * @param file DOCX-файл
     * @return содержимое документа в виде строки
     * @throws IOException при ошибках чтения файла
     */
    private String extractTextFromDocx(MultipartFile file) throws IOException {
        try (XWPFDocument doc = new XWPFDocument(file.getInputStream())) {
            return doc.getParagraphs().stream()
                    .map(XWPFParagraph::getText)
                    .reduce("", (acc, text) -> acc + "\n" + text);
        }
    }
}
