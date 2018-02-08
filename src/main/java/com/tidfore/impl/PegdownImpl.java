package com.tidfore.impl;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 2018/2/6 0006
 * Time: 22:35
 * To change this template use File | Settings | File Templates.
 */
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import com.tidfore.Markdown;
import org.apache.commons.lang.StringEscapeUtils;
import org.parboiled.common.StringUtils;
import org.pegdown.LinkRenderer;
import org.pegdown.PegDownProcessor;
import org.pegdown.Printer;
import org.pegdown.ToHtmlSerializer;
import org.pegdown.VerbatimSerializer;
import org.pegdown.ast.CodeNode;
import org.pegdown.ast.Node;
import org.pegdown.ast.RootNode;
import org.pegdown.ast.VerbatimNode;
import org.pegdown.ast.Visitor;
import org.pegdown.plugins.PegDownPlugins;
import org.pegdown.plugins.ToHtmlSerializerPlugin;
import org.pegdown.plugins.PegDownPlugins.Builder;

public class PegdownImpl implements Markdown {
    private PegDownProcessor processor;

    public PegdownImpl() {
        PegDownPlugins plugins = (new Builder()).withHtmlSerializer(new ToHtmlSerializerPlugin[]{new CustomToHtmlSerializerPlugin( )}).build();
        this.processor = new PegDownProcessor(65535);
    }

    public String parse(String text) {
        RootNode node = this.processor.parseMarkdown(text.toCharArray());
        List<ToHtmlSerializerPlugin> serializePlugins = Arrays.asList(new CustomToHtmlSerializerPlugin( ));
        String finalHtml = (new CustomToHtmlSerializer(new LinkRenderer(), serializePlugins)).toHtml(node);
        return finalHtml;
    }

    private static class CustomToHtmlSerializer extends ToHtmlSerializer {
        public CustomToHtmlSerializer(LinkRenderer linkRenderer) {
            super(linkRenderer);
        }

        public CustomToHtmlSerializer(LinkRenderer linkRenderer, List<ToHtmlSerializerPlugin> plugins) {
            super(linkRenderer, plugins);
        }

        public CustomToHtmlSerializer(LinkRenderer linkRenderer, Map<String, VerbatimSerializer> verbatimSerializers, List<ToHtmlSerializerPlugin> plugins) {
            super(linkRenderer, verbatimSerializers, plugins);
        }

        public CustomToHtmlSerializer(LinkRenderer linkRenderer, Map<String, VerbatimSerializer> verbatimSerializers) {
            super(linkRenderer, verbatimSerializers);
        }

        public void visit(CodeNode node) {
            String preTag = "pre";
            String codeTag = "code";
            boolean needPre = false;

            String text;
            for (text = node.getText(); text.charAt(0) == '\n' || text.charAt(0) == '\r'; text = text.substring(1)) {

            }

            needPre = text.contains("\n");
            String escapeHtml = StringEscapeUtils.escapeHtml(text);
            if (needPre) {
                this.printer.print('<').print(preTag).print('>');
            }

            this.printer.print('<').print(codeTag).print('>');
            this.printer.print(escapeHtml);
            this.printer.print('<').print('/').print(codeTag).print('>');
            if (needPre) {
                this.printer.print('<').print('/').print(preTag).print('>');
            }

        }
    }

    private static class CustomToHtmlSerializerPlugin implements ToHtmlSerializerPlugin {
        private CustomToHtmlSerializerPlugin() {
        }

        public boolean visit(Node node, Visitor visitor, Printer printer) {
            System.out.println(node);
            System.out.println(visitor);
            return true;
        }
    }

    private static class CustomVerbatimSerializer implements VerbatimSerializer {
        private CustomVerbatimSerializer() {
        }

        public void serialize(VerbatimNode node, Printer printer) {
            printer.println().print("<pre><code");
            if (!StringUtils.isEmpty(node.getType())) {
                this.printAttribute(printer, "class", node.getType());
            }

            printer.print(">");

            String text;
            for (text = node.getText(); text.charAt(0) == '\n'; text = text.substring(1)) {
                printer.print("<br/>");
            }

            String all = text.replaceAll("\n", "<br/>").replaceAll("\\t", "&nbsp;&nbsp;&nbsp;&nbsp;").replaceAll(" ", "&nbsp;");
            System.out.println(all);
            printer.printEncoded(all);
            printer.print("</code></pre>");
        }

        private void printAttribute(Printer printer, String name, String value) {
            printer.print(' ').print(name).print('=').print('"').print(value).print('"');
        }
    }
}

