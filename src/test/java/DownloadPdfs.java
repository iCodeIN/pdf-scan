import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.PageRange;
import com.itextpdf.kernel.utils.PdfSplitter;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.*;
import java.net.URISyntaxException;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DownloadPdfs {

    private static Random RANDOM = new Random(System.currentTimeMillis());
    private static String USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36";

    public static String buildURL(String[] terms, int page) {
        String q = "";
        for (String t : terms) {
            q += t + "+";
        }
        q = q.substring(0, q.length() - 1);
        return "https://www.google.com/search?q=" + q + "+filetype:pdf&ei=iJqoXLKNMdDLwQL3rq6oDw&start=" + (page * 10) + "&sa=N&ved=0ahUKEwiy8YeAu7vhAhXQZVAKHXeXC_UQ8NMDCKkB&biw=1920&bih=916";
    }

    public static void fetchPdfURLs(String[] terms, int page) throws IOException, URISyntaxException {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(buildURL(terms, page));

        // add request header
        request.addHeader("User-Agent", USER_AGENT);
        HttpResponse response = client.execute(request);

        // build result
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        rd.close();

        //
        String txt = result.toString();

        Matcher matcher = Pattern.compile("href=\"([^\"]+)\"").matcher(txt);
        while (matcher.find()) {
            String url = matcher.group(1);
            if (url.endsWith(".pdf")) {

                // copy
                File outputPDF = copyPdf(url);

                // copy first page
                copyFirstPage(outputPDF);
            }
        }
    }

    private static File copyPdf(String url) throws URISyntaxException, IOException {
        File out = new File("corpus");
        if (!out.exists())
            out.mkdir();
        out = new File(out, "document_" + out.listFiles().length + ".pdf");
        try {
            IOUtils.copy(new java.net.URI(url).toURL().openStream(), new FileOutputStream(out));
        } catch (Exception ex) { }
        return out;
    }

    private static File copyFirstPage(File inputFile) throws IOException {
        String name = inputFile.getName().replaceAll("\\.pdf","") + "_single_page.pdf";
        final File outputFile = new File(inputFile.getParentFile(), name);

        try {
            PdfDocument pdfDocument = new PdfDocument(new PdfReader(inputFile));
            new PdfSplitter(pdfDocument) {
                @Override
                protected PdfWriter getNextPdfWriter(PageRange documentPageRange) {
                    try {
                        return new PdfWriter(outputFile);
                    } catch (FileNotFoundException e) {
                    }
                    return null;
                }
            }.extractPageRange(new PageRange("1-1")).close();
        }catch (Throwable ex){}

        return outputFile;
    }

    public static void fetchPdfURLs(String[] terms) throws IOException, URISyntaxException {
        for (int i = 1; i < 20; i++) {
            // lookup
            fetchPdfURLs(terms, i);
            // sleep
            try {
                Thread.sleep(5000 + RANDOM.nextInt(3) * 1000);
            } catch (InterruptedException e) { }
        }
    }


    public static void main(String[] args) throws IOException, URISyntaxException {
        fetchPdfURLs(new String[]{"safety","procedures","leaflet"});
    }
}
