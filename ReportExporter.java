import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Creates portable CSV and PDF reports using only JDK APIs. */
final class ReportExporter {
    private ReportExporter() { }

    static String csv(String value) {
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    static void writeCsv(Path path, List<String[]> rows) throws Exception {
        StringBuilder csv = new StringBuilder("skill,category,current_score,level,target,gap,last_assessed\r\n");
        for (String[] row : rows) {
            for (int i = 0; i < row.length; i++) {
                if (i > 0) csv.append(',');
                csv.append(csv(row[i]));
            }
            csv.append("\r\n");
        }
        Files.writeString(path, csv, StandardCharsets.UTF_8);
    }

    static void writePdf(Path path, String text) throws Exception {
        List<String> lines = new ArrayList<>();
        for (String line : text.split("\n")) {
            String remaining = line;
            while (remaining.length() > 100) {
                lines.add(remaining.substring(0, 100));
                remaining = remaining.substring(100);
            }
            lines.add(remaining);
        }

        StringBuilder stream = new StringBuilder("BT /F1 10 Tf 50 790 Td 13 TL\n");
        for (String line : lines) stream.append('(').append(escapePdf(line)).append(") Tj T*\n");
        stream.append("ET");
        String pageStream = stream.toString();
        List<String> objects = List.of(
                "<< /Type /Catalog /Pages 2 0 R >>",
                "<< /Type /Pages /Kids [3 0 R] /Count 1 >>",
                "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 612 842] /Resources << /Font << /F1 4 0 R >> >> /Contents 5 0 R >>",
                "<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>",
                "<< /Length " + pageStream.getBytes(StandardCharsets.US_ASCII).length + " >>\nstream\n" + pageStream + "\nendstream");

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        output.write("%PDF-1.4\n".getBytes(StandardCharsets.US_ASCII));
        List<Integer> offsets = new ArrayList<>();
        for (int i = 0; i < objects.size(); i++) {
            offsets.add(output.size());
            output.write(((i + 1) + " 0 obj\n" + objects.get(i) + "\nendobj\n")
                    .getBytes(StandardCharsets.US_ASCII));
        }
        int xref = output.size();
        output.write(("xref\n0 " + (objects.size() + 1) + "\n0000000000 65535 f \n")
                .getBytes(StandardCharsets.US_ASCII));
        for (int offset : offsets) {
            output.write(String.format(Locale.ROOT, "%010d 00000 n \n", offset)
                    .getBytes(StandardCharsets.US_ASCII));
        }
        output.write(("trailer << /Size " + (objects.size() + 1) + " /Root 1 0 R >>\nstartxref\n"
                + xref + "\n%%EOF").getBytes(StandardCharsets.US_ASCII));
        Files.write(path, output.toByteArray());
    }

    private static String escapePdf(String text) {
        return text.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)")
                .replaceAll("[^\\x20-\\x7E]", "?");
    }
}
