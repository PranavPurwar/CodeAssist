package com.tyron.code.ui.editor.language.kotlin;

import android.graphics.Color;
import android.util.Log;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.Token;

import java.util.Stack;

import io.github.rosemoe.editor.interfaces.CodeAnalyzer;
import io.github.rosemoe.editor.struct.BlockLine;
import io.github.rosemoe.editor.struct.Span;
import io.github.rosemoe.editor.text.TextAnalyzeResult;
import io.github.rosemoe.editor.text.TextAnalyzer;
import io.github.rosemoe.editor.widget.EditorColorScheme;

public class KotlinAnalyzer implements CodeAnalyzer {

    @Override
    public void analyze(CharSequence content, TextAnalyzeResult colors, TextAnalyzer.AnalyzeThread.Delegate delegate) {
        try {
            CodePointCharStream stream = CharStreams.fromString(String.valueOf(content));
            KotlinLexer lexer = new KotlinLexer(stream);

            Stack<BlockLine> stack = new Stack<>();
            int maxSwitch = 1, currSwitch = 0;
            int lastLine = 0;
            int line, column;
            Token token = null;

            while (delegate.shouldAnalyze()) {
                token = lexer.nextToken();
                if (token == null) {
                    break;
                }

                if (token.getType() == KotlinLexer.EOF) {
                    lastLine = token.getLine() - 1;
                    break;
                }
                line = token.getLine() - 1;
                column = token.getCharPositionInLine();
                lastLine = line;

                switch (token.getType()) {
                    case KotlinLexer.ADD:
                    case KotlinLexer.SUB:
                    case KotlinLexer.MULT:
                    case KotlinLexer.DIV:
                        colors.addIfNeeded(line, column, EditorColorScheme.OPERATOR);
                        break;
                    case KotlinLexer.FUN:
                    case KotlinLexer.SUSPEND:
                    case KotlinLexer.OVERRIDE:
                    case KotlinLexer.CLASS:
                    case KotlinLexer.OPEN:
                    case KotlinLexer.PRIVATE:
                    case KotlinLexer.PUBLIC:
                    case KotlinLexer.PROTECTED:
                    case KotlinLexer.DATA:
                    case KotlinLexer.CONSTRUCTOR:
                    case KotlinLexer.VAL:
                    case KotlinLexer.VAR:
                    case KotlinLexer.VARARG:
                    case KotlinLexer.SEALED:
                    case KotlinLexer.PACKAGE:
                    case KotlinLexer.IMPORT:
                    case KotlinLexer.ABSTRACT:
                    case KotlinLexer.CATCH:
                    case KotlinLexer.THROW:
                    case KotlinLexer.CONTINUE:
                    case KotlinLexer.FOR:
                    case KotlinLexer.WHEN:
                    case KotlinLexer.WHILE:
                    case KotlinLexer.FINAL:
                    case KotlinLexer.LATEINIT:
                    case KotlinLexer.IN:
                    case KotlinLexer.INFIX:
                    case KotlinLexer.AS:
                    case KotlinLexer.INLINE:
                        colors.addIfNeeded(line, column, EditorColorScheme.KEYWORD);
                        break;
                    case KotlinLexer.Identifier:
                        colors.addIfNeeded(line, column, EditorColorScheme.IDENTIFIER_NAME);
                        break;
                    case KotlinLexer.MultiLineString:
                    case KotlinLexer.LineString:
                    case KotlinLexer.StringExpression:
                    case KotlinLexer.IntegerLiteral:
                    case KotlinLexer.CharacterLiteral:
                    case KotlinLexer.BinLiteral:
                    case KotlinLexer.RealLiteral:
                    case KotlinLexer.BooleanLiteral:
                    case KotlinLexer.DoubleLiteral:
                    case KotlinLexer.FloatLiteral:
                    case KotlinLexer.LongLiteral:
                    case KotlinLexer.HexLiteral:
                        Span span = Span.obtain(column, EditorColorScheme.LITERAL);
                        if (token.getType() == KotlinLexer.HexLiteral) {
                            try {
                                span.setUnderlineColor(Integer.parseInt(token.getText(), 16));
                            } catch (Exception e) {
                                span.setUnderlineColor(Color.TRANSPARENT);
                            }
                        }
                        colors.addIfNeeded(line, span);
                        break;
                    case KotlinLexer.ANNOTATION:
                        colors.addIfNeeded(line, column, EditorColorScheme.ANNOTATION);
                        break;
                    case KotlinLexer.LCURL:
                        if (stack.isEmpty()) {
                            if (currSwitch > maxSwitch) {
                                maxSwitch = currSwitch;
                            }
                            currSwitch = 0;
                        }
                        currSwitch++;
                        BlockLine block = colors.obtainNewBlock();
                        block.startLine = line;
                        block.startColumn = column;
                        stack.push(block);
                        break;
                    case KotlinLexer.RCURL:
                        if (!stack.isEmpty()) {
                            BlockLine b = stack.pop();
                            b.endLine = line;
                            b.endColumn = column;
                            if (b.startLine != b.endLine) {
                                colors.addBlockLine(b);
                            }
                        }
                        break;
                    default:
                        colors.addIfNeeded(line, column, EditorColorScheme.TEXT_NORMAL);

                }
            }
            colors.determine(lastLine);

            if (stack.isEmpty()) {
                if (currSwitch > maxSwitch) {
                    maxSwitch = currSwitch;
                }
            }
            colors.setSuppressSwitch(maxSwitch + 10);
        } catch (Exception ignore) {}
    }
}
