package org.foxtrot.enums;

public enum DocumentFileExtension {
    TXT("tinyTXT.txt"),
    CSV("tinyCSV.csv"),
    DOCX("tinyDOCX.docx"),
    XLS("simple1.xls");

    private final String fileName;

    DocumentFileExtension(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
