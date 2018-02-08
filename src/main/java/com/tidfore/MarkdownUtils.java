package com.tidfore;


import com.tidfore.impl.PegdownImpl;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.util.Iterator;

public class MarkdownUtils {

    private static Markdown markdown = new PegdownImpl();

    public MarkdownUtils() {
    }

    public static void toHtml(String markdownPath, String toHtmlPath) throws IOException {
        toHtml(new File(markdownPath), new File(toHtmlPath));
    }

    public static void toHtml(File markdownFile, File toHtmlFile) throws IOException {
        String name = toHtmlFile.getName();
        toHtml(new FileInputStream(markdownFile), new FileOutputStream(toHtmlFile), name.replaceAll(".html", ""),markdownFile.getParent());
    }

    public static void toHtml(InputStream markdownStream, OutputStream toHtmlStream, String title,String path) throws IOException {
        ByteArrayOutputStream baos = null;
        BufferedWriter writer = null;
        try {
            baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = markdownStream.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            byte[] byteArray = baos.toByteArray();
            String bodyHtml = markdown.parse(new String(byteArray));
            String body = getBody(bodyHtml,path);
            String html = getTemplate(title, body);
            writer = new BufferedWriter(new OutputStreamWriter(toHtmlStream));
            writer.write(html);
        } finally {
            closeIO(markdownStream);
            closeIO(baos);
            closeIO(writer);
            closeIO(toHtmlStream);
        }
    }
    private static void closeIO(Closeable io) {
        if (io != null) {
            try {
                io.close();
            } catch (IOException var2) {
                var2.printStackTrace();
            }
            io = null;
        }
    }

    private static String getBody(String bodyHtml,String path) {
        Document doc = Jsoup.parse(bodyHtml);
        Elements imgs = doc.select("img[src]");
        if (imgs != null) {
            Iterator var4 = imgs.iterator();
            while (var4.hasNext()) {
                Element element = (Element) var4.next();
                String src = element.attr("src");
                try {
                    String image;
                    if(src.contains(":")){
                        image = base64Image(src);
                    }else{
                        image = base64Image(path+"/"+src);
                    }
                    element.attr("src", "data:image/jpg;base64," + image);
                } catch (IOException var7) {
                    var7.printStackTrace();
                }
            }
        }

        return doc.toString();
    }

    public static String base64Image(String src) throws IOException {
        FileInputStream fis = null;
        ByteArrayOutputStream baos = null;
        try {
            String file = src;
            if (src.startsWith("file:")) {
                URI uri = URI.create(src);
                URL url = uri.toURL();
                file = url.getFile();
            }
            baos = new ByteArrayOutputStream();
            fis = new FileInputStream(new File(file));
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            byte[] data = baos.toByteArray();
            String var8 = new String(Base64.encodeBase64(data));
            return var8;
        } finally {
            closeIO(fis);
            closeIO(baos);
        }
    }

    private static String getTemplate(String title, String body) {
        VelocityEngine ve = new VelocityEngine();
        ve.setProperty("resource.loader", "classpath");
        ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        ve.setProperty("ISO-8859-1", "utf-8");
        ve.setProperty("input.encoding", "utf-8");
        ve.setProperty("output.encoding", "utf-8");
        ve.init();
        Template template = ve.getTemplate("vm/html.vm");
        VelocityContext ctx = new VelocityContext();
        ctx.put("title", title);

        try {
            ctx.put("css",             getJsOrcss("/css/github-markdown.css"));
            ctx.put("treeCSS",         getJsOrcss("/css/zTreeStyle.css"));
            ctx.put("jqueryJS",        getJsOrcss("/js/jquery-1.4.4.min.js"));
            ctx.put("treeCodeJS",      getJsOrcss("/js/jquery.ztree.all-3.5.min.js"));
            ctx.put("treeTocJS",       getJsOrcss("/js/ztree_toc.js"));
            ctx.put("highlightCSS",    getJsOrcss("/css/highlight.css"));
            ctx.put("highlight",       getJsOrcss("/js/highlight.js"));
            ctx.put("highlightnumber", getJsOrcss("/js/highlightjs-line-numbers.min.js"));
        } catch (IOException var6) {
            var6.printStackTrace();
        }

        ctx.put("body", body);
        StringWriter sw = new StringWriter();
        template.merge(ctx, sw);
        return sw.toString();
    }

    private static String getJsOrcss(String src) throws IOException {
        InputStream fis = null;
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            fis = MarkdownUtils.class.getResourceAsStream(src);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            String text = baos.toString();
            text = text.replaceAll("(\r\n|\r|\n|\n\r)", "");
            String var7 = text;
            return var7;
        } finally {
            closeIO(fis);
            closeIO(baos);
        }
    }

    private static String htmlConvert(String str) {
        return StringEscapeUtils.escapeHtml(str);
    }

}
