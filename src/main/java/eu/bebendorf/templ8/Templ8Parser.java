package eu.bebendorf.templ8;

import eu.bebendorf.purejavaparser.PureJavaParser;
import eu.bebendorf.purejavaparser.ast.Variable;
import eu.bebendorf.purejavaparser.ast.expression.Expression;
import eu.bebendorf.purejavaparser.exception.UnexpectedCharacterException;
import eu.bebendorf.purejavaparser.parser.UnexpectedTokenException;
import eu.bebendorf.purejavaparser.token.Token;
import eu.bebendorf.purejavaparser.token.TokenStack;
import eu.bebendorf.purejavaparser.token.TokenType;
import eu.bebendorf.purejavaparser.token.Tokenizer;
import eu.bebendorf.templ8.macro.*;
import eu.bebendorf.templ8.macro.control.*;
import eu.bebendorf.templ8.macro.layout.ExtendsMacro;
import eu.bebendorf.templ8.macro.layout.IncludeMacro;
import eu.bebendorf.templ8.macro.layout.SectionMacro;
import eu.bebendorf.templ8.macro.layout.YieldMacro;
import eu.bebendorf.templ8.source.TemplateFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Templ8Parser {

    private static final TokenType BREAK_MACRO = new TokenType("BREAK_MACRO", "@break", false);
    private static final TokenType CONTINUE_MACRO = new TokenType("CONTINUE_MACRO", "@continue", false);

    private static final TokenType IMPORT_MACRO = new TokenType("IMPORT_MACRO", "@import", false);

    private static final TokenType INCLUDE_MACRO = new TokenType("INCLUDE_MACRO", "@include", false);
    private static final TokenType EXTENDS_MACRO = new TokenType("EXTENDS_MACRO", "@extends", false);
    private static final TokenType SECTION_MACRO = new TokenType("SECTION_MACRO", "@section", false);
    private static final TokenType ENDSECTION_MACRO = new TokenType("ENDSECTION_MACRO", "@endsection", false);
    private static final TokenType YIELD_MACRO = new TokenType("YIELD_MACRO", "@yield", false);

    private static final TokenType IF_MACRO = new TokenType("IF_MACRO", "@if", false);
    private static final TokenType ELSEIF_MACRO = new TokenType("ELSEIF_MACRO", "@elseif", false);
    private static final TokenType ELSE_MACRO = new TokenType("ELSE_MACRO", "@else", false);
    private static final TokenType ENDIF_MACRO = new TokenType("ENDIF_MACRO", "@endif", false);

    private static final TokenType UNLESS_MACRO = new TokenType("UNLESS_MACRO", "@unless", false);
    private static final TokenType ENDUNLESS_MACRO = new TokenType("ENDUNLESS_MACRO", "@endunless", false);

    private static final TokenType EMPTY_MACRO = new TokenType("EMPTY_MACRO", "@empty", false);
    private static final TokenType ENDEMPTY_MACRO = new TokenType("ENDEMPTY_MACRO", "@endempty", false);

    private static final TokenType ISSET_MACRO = new TokenType("ISSET_MACRO", "@isset", false);
    private static final TokenType ENDISSET_MACRO = new TokenType("ENDISSET_MACRO", "@endisset", false);

    private static final TokenType SWITCH_MACRO = new TokenType("SWITCH_MACRO", "@switch", false);
    private static final TokenType CASE_MACRO = new TokenType("CASE_MACRO", "@case", false);
    private static final TokenType DEFAULT_MACRO = new TokenType("DEFAULT_MACRO", "@default", false);
    private static final TokenType ENDSWITCH_MACRO = new TokenType("ENDSWITCH_MACRO", "@endswitch", false);

    private static final TokenType WHILE_MACRO = new TokenType("WHILE_MACRO", "@while", false);
    private static final TokenType ENDWHILE_MACRO = new TokenType("ENDWHILE_MACRO", "@endwhile", false);

    private static final TokenType FOR_MACRO = new TokenType("FOR_MACRO", "@for", false);
    private static final TokenType ENDFOR_MACRO = new TokenType("ENDFOR_MACRO", "@endfor", false);

    private static final TokenType FOREACH_MACRO = new TokenType("FOREACH_MACRO", "@foreach", false);
    private static final TokenType ENDFOREACH_MACRO = new TokenType("ENDFOREACH_MACRO", "@endforeach", false);

    private static final TokenType FORELSE_MACRO = new TokenType("FORELSE_MACRO", "@forelse", false);
    private static final TokenType ENDFORELSE_MACRO = new TokenType("ENDFORELSE_MACRO", "@endforelse", false);

    private static final TokenType ANY_TEXT = new TokenType("ANY_TEXT", "[\\s\\S]", false);

    private static final TokenType[] BLADE4J_TOKEN_TYPES = {
        // Control
        BREAK_MACRO,
        CONTINUE_MACRO,
        // Import
        IMPORT_MACRO,
        // Layout
        INCLUDE_MACRO,
        EXTENDS_MACRO,
        SECTION_MACRO,
        ENDSECTION_MACRO,
        YIELD_MACRO,
        // If
        IF_MACRO,
        ELSEIF_MACRO,
        ELSE_MACRO,
        ENDIF_MACRO,
        // Unless
        UNLESS_MACRO,
        ENDUNLESS_MACRO,
        // Isset
        ISSET_MACRO,
        ENDISSET_MACRO,
        // Empty
        EMPTY_MACRO,
        ENDEMPTY_MACRO,
        // Switch
        SWITCH_MACRO,
        CASE_MACRO,
        DEFAULT_MACRO,
        ENDSWITCH_MACRO,
        // While
        WHILE_MACRO,
        ENDWHILE_MACRO,
        // For-Each
        FOREACH_MACRO,
        ENDFOREACH_MACRO,
        // For-Else
        FORELSE_MACRO,
        ENDFORELSE_MACRO,
        // For
        FOR_MACRO,
        ENDFOR_MACRO,
    };

    private TokenType[] tokenTypes;
    private PureJavaParser javaParser = new PureJavaParser();

    public Templ8Parser() {
        TokenType[] defaultJava = TokenType.defaultJava();
        tokenTypes = new TokenType[BLADE4J_TOKEN_TYPES.length + defaultJava.length + 1];
        System.arraycopy(BLADE4J_TOKEN_TYPES, 0, tokenTypes, 0, BLADE4J_TOKEN_TYPES.length);
        System.arraycopy(defaultJava, 0, tokenTypes, BLADE4J_TOKEN_TYPES.length, defaultJava.length);
        tokenTypes[tokenTypes.length - 1] = ANY_TEXT;
    }

    public Templ8Template parse(TemplateFile file) throws UnexpectedCharacterException, UnexpectedTokenException {
        TokenStack tokenStack = Tokenizer.tokenize(tokenTypes, file.getFileName(), file.getSource());
        MacroList content = parseMacroList(tokenStack, Arrays.asList("EOF"), false);
        return new Templ8Template(file, content);
    }

    private MacroList parseMacroList(TokenStack stack, List<String> expectedEnds, boolean trim) throws UnexpectedTokenException {
        List<Macro> nodes = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean trimLeft = trim;
        outer:
        while (true) {
            Token t = stack.peek();
            switch (t.getType().getName()) {
                case "BREAK_MACRO":
                    stack.pop();
                    if(sb.length() > 0) {
                        nodes.add(new TextMacro(sb.toString(), trimLeft, false));
                        trimLeft = false;
                        sb = new StringBuilder();
                    }
                    nodes.add(new BreakMacro());
                    break;
                case "CONTINUE_MACRO":
                    stack.pop();
                    if(sb.length() > 0) {
                        nodes.add(new TextMacro(sb.toString(), trimLeft, false));
                        trimLeft = false;
                        sb = new StringBuilder();
                    }
                    nodes.add(new ContinueMacro());
                    break;
                case "IMPORT_MACRO":
                    if(sb.length() > 0) {
                        nodes.add(new TextMacro(sb.toString(), trimLeft, false));
                        trimLeft = false;
                        sb = new StringBuilder();
                    }
                    nodes.add(parseImportMacro(stack));
                    break;
                case "INCLUDE_MACRO":
                    if(sb.length() > 0) {
                        nodes.add(new TextMacro(sb.toString(), trimLeft, false));
                        trimLeft = false;
                        sb = new StringBuilder();
                    }
                    nodes.add(parseIncludeMacro(stack));
                    break;
                case "EXTENDS_MACRO":
                    if(sb.length() > 0) {
                        nodes.add(new TextMacro(sb.toString(), trimLeft, false));
                        trimLeft = false;
                        sb = new StringBuilder();
                    }
                    nodes.add(parseExtendsMacro(stack));
                    break;
                case "SECTION_MACRO":
                    if(sb.length() > 0) {
                        nodes.add(new TextMacro(sb.toString(), trimLeft, false));
                        trimLeft = false;
                        sb = new StringBuilder();
                    }
                    nodes.add(parseSectionMacro(stack));
                    break;
                case "YIELD_MACRO":
                    if(sb.length() > 0) {
                        nodes.add(new TextMacro(sb.toString(), trimLeft, false));
                        trimLeft = false;
                        sb = new StringBuilder();
                    }
                    nodes.add(parseYieldMacro(stack));
                    break;
                case "IF_MACRO":
                    if(sb.length() > 0) {
                        nodes.add(new TextMacro(sb.toString(), trimLeft, false));
                        trimLeft = false;
                        sb = new StringBuilder();
                    }
                    nodes.add(parseIfMacro(stack));
                    break;
                case "UNLESS_MACRO":
                    if(sb.length() > 0) {
                        nodes.add(new TextMacro(sb.toString(), trimLeft, false));
                        trimLeft = false;
                        sb = new StringBuilder();
                    }
                    nodes.add(parseUnlessMacro(stack));
                    break;
                case "ISSET_MACRO":
                    if(sb.length() > 0) {
                        nodes.add(new TextMacro(sb.toString(), trimLeft, false));
                        trimLeft = false;
                        sb = new StringBuilder();
                    }
                    nodes.add(parseIssetMacro(stack));
                    break;
                case "WHILE_MACRO":
                    if(sb.length() > 0) {
                        nodes.add(new TextMacro(sb.toString(), trimLeft, false));
                        trimLeft = false;
                        sb = new StringBuilder();
                    }
                    nodes.add(parseWhileMacro(stack));
                    break;
                case "FOREACH_MACRO":
                    if(sb.length() > 0) {
                        nodes.add(new TextMacro(sb.toString(), trimLeft, false));
                        trimLeft = false;
                        sb = new StringBuilder();
                    }
                    nodes.add(parseForEachMacro(stack));
                    break;
                case "FORELSE_MACRO":
                    if(sb.length() > 0) {
                        nodes.add(new TextMacro(sb.toString(), trimLeft, false));
                        trimLeft = false;
                        sb = new StringBuilder();
                    }
                    nodes.add(parseForElseMacro(stack));
                    break;
                case "EOF":
                case "ENDIF_MACRO":
                case "ELSEIF_MACRO":
                case "ELSE_MACRO":
                case "ENDUNLESS_MACRO":
                case "ENDISSET_MACRO":
                case "ENDEMPTY_MACRO":
                case "ENDSWITCH_MACRO":
                case "DEFAULT_MACRO":
                case "CASE_MACRO":
                case "EMPTY_MACRO":
                case "ENDWHILE_MACRO":
                case "ENDFOR_MACRO":
                case "ENDFOREACH_MACRO":
                case "ENDSECTION_MACRO":
                case "ENDFORELSE_MACRO": {
                    if(t.getType() == EMPTY_MACRO) {
                        TokenStack stackCopy = stack.clone();
                        stackCopy.pop();
                        if(stackCopy.trim().peek().getType() == TokenType.GROUP_START) {
                            if (sb.length() > 0) {
                                nodes.add(new TextMacro(sb.toString(), trimLeft, trim));
                                trimLeft = false;
                                sb = new StringBuilder();
                            }
                            nodes.add(parseEmptyMacro(stack));
                            break;
                        }
                    }
                    if (expectedEnds.contains(t.getType().getName())) {
                        if (sb.length() > 0)
                            nodes.add(new TextMacro(sb.toString(), false, trim));
                        break outer;
                    }
                    throw new UnexpectedTokenException(stack.pop());
                }
                default: {
                    if(t.getType() == TokenType.OPEN_CURLY_BRACKET) {
                        TokenStack stackCopy = stack.clone();
                        stackCopy.pop();
                        if(stackCopy.peek().getType() == TokenType.OPEN_CURLY_BRACKET) {
                            if(sb.length() > 0) {
                                nodes.add(new TextMacro(sb.toString(), trimLeft, false));
                                trimLeft = false;
                                sb = new StringBuilder();
                            }
                            nodes.add(parseInterpolation(stack));
                            break;
                        }
                        if(stackCopy.peek().getType() == TokenType.NOT_OP && stackCopy.pop().getType() == TokenType.NOT_OP) {
                            if(sb.length() > 0) {
                                nodes.add(new TextMacro(sb.toString(), trimLeft, false));
                                trimLeft = false;
                                sb = new StringBuilder();
                            }
                            nodes.add(parseInterpolation(stack));
                            break;
                        }
                    }
                    stack.pop();
                    sb.append(t.getValue());
                }
            }
        }
        return new MacroList(nodes);
    }

    private InterpolationMacro parseInterpolation(TokenStack stack) throws UnexpectedTokenException {
        if(stack.peek().getType() != TokenType.OPEN_CURLY_BRACKET)
            throw new UnexpectedTokenException(stack.pop());
        stack.pop();
        boolean raw = false;
        if(stack.peek().getType() == TokenType.NOT_OP) {
            stack.pop();
            if(stack.peek().getType() != TokenType.NOT_OP)
                throw new UnexpectedTokenException(stack.pop());
            stack.pop();
            raw = true;
        } else if(stack.peek().getType() == TokenType.OPEN_CURLY_BRACKET) {
            stack.pop();
        } else {
            throw new UnexpectedTokenException(stack.pop());
        }
        Expression expression = javaParser.getExpressionParser().parseExpression(stack);
        if(raw) {
            if(stack.trim().peek().getType() != TokenType.NOT_OP)
                throw new UnexpectedTokenException(stack.pop());
            stack.pop();
            if(stack.peek().getType() != TokenType.NOT_OP)
                throw new UnexpectedTokenException(stack.pop());
        } else {
            if(stack.trim().peek().getType() != TokenType.CLOSE_CURLY_BRACKET)
                throw new UnexpectedTokenException(stack.pop());
        }
        stack.pop();
        if(stack.peek().getType() != TokenType.CLOSE_CURLY_BRACKET)
            throw new UnexpectedTokenException(stack.pop());
        stack.pop();
        return new InterpolationMacro(expression, raw);
    }

    private ImportMacro parseImportMacro(TokenStack stack) throws UnexpectedTokenException {
        if(stack.peek().getType() != IMPORT_MACRO)
            throw new UnexpectedTokenException(stack.pop());
        stack.pop();
        if(stack.trim().peek().getType() != TokenType.GROUP_START)
            throw new UnexpectedTokenException(stack.pop());
        stack.pop();
        Expression classExpression = javaParser.getExpressionParser().parseExpression(stack);
        Expression nameExpression = null;
        if(stack.trim().peek().getType() == TokenType.SEPERATOR) {
            stack.pop();
            nameExpression = classExpression;
            classExpression = javaParser.getExpressionParser().parseExpression(stack);
        }
        if(stack.trim().peek().getType() != TokenType.GROUP_END)
            throw new UnexpectedTokenException(stack.pop());
        stack.pop();
        return new ImportMacro(nameExpression, classExpression);
    }

    private IncludeMacro parseIncludeMacro(TokenStack stack) throws UnexpectedTokenException {
        if(stack.peek().getType() != INCLUDE_MACRO)
            throw new UnexpectedTokenException(stack.pop());
        stack.pop();
        if(stack.trim().peek().getType() != TokenType.GROUP_START)
            throw new UnexpectedTokenException(stack.pop());
        stack.pop();
        Expression nameExpression = javaParser.getExpressionParser().parseExpression(stack);
        if(stack.trim().peek().getType() != TokenType.GROUP_END)
            throw new UnexpectedTokenException(stack.pop());
        stack.pop();
        return new IncludeMacro(nameExpression);
    }

    private YieldMacro parseYieldMacro(TokenStack stack) throws UnexpectedTokenException {
        if(stack.peek().getType() != YIELD_MACRO)
            throw new UnexpectedTokenException(stack.pop());
        stack.pop();
        if(stack.trim().peek().getType() != TokenType.GROUP_START)
            throw new UnexpectedTokenException(stack.pop());
        stack.pop();
        Expression nameExpression = javaParser.getExpressionParser().parseExpression(stack);
        Expression fallbackBody = null;
        if(stack.trim().peek().getType() == TokenType.SEPERATOR) {
            stack.pop();
            fallbackBody = javaParser.getExpressionParser().parseExpression(stack);
        }
        if(stack.trim().peek().getType() != TokenType.GROUP_END)
            throw new UnexpectedTokenException(stack.pop());
        stack.pop();
        return new YieldMacro(nameExpression, fallbackBody);
    }

    private ExtendsMacro parseExtendsMacro(TokenStack stack) throws UnexpectedTokenException {
        if(stack.peek().getType() != EXTENDS_MACRO)
            throw new UnexpectedTokenException(stack.pop());
        stack.pop();
        if(stack.trim().peek().getType() != TokenType.GROUP_START)
            throw new UnexpectedTokenException(stack.pop());
        stack.pop();
        Expression nameExpression = javaParser.getExpressionParser().parseExpression(stack);
        if(stack.trim().peek().getType() != TokenType.GROUP_END)
            throw new UnexpectedTokenException(stack.pop());
        stack.pop();
        return new ExtendsMacro(nameExpression);
    }

    private SectionMacro parseSectionMacro(TokenStack stack) throws UnexpectedTokenException {
        if(stack.peek().getType() != SECTION_MACRO)
            throw new UnexpectedTokenException(stack.pop());
        stack.pop();
        if(stack.trim().peek().getType() != TokenType.GROUP_START)
            throw new UnexpectedTokenException(stack.pop());
        stack.pop();
        Expression nameExpression = javaParser.getExpressionParser().parseExpression(stack);
        Expression fallbackBody = null;
        if(stack.trim().peek().getType() == TokenType.SEPERATOR) {
            stack.pop();
            fallbackBody = javaParser.getExpressionParser().parseExpression(stack);
        }
        if(stack.trim().peek().getType() != TokenType.GROUP_END)
            throw new UnexpectedTokenException(stack.pop());
        stack.pop();
        MacroList body = null;
        if(fallbackBody == null) {
            body = parseMacroList(stack, Arrays.asList("ENDSECTION_MACRO"), true);
            stack.pop();
        }
        return new SectionMacro(nameExpression, fallbackBody, body);
    }

    private IfMacro parseIfMacro(TokenStack stack) throws UnexpectedTokenException {
        if(stack.peek().getType() != IF_MACRO)
            throw new UnexpectedTokenException(stack.pop());
        stack.pop();
        if(stack.trim().peek().getType() != TokenType.GROUP_START)
            throw new UnexpectedTokenException(stack.pop());
        stack.pop();
        Expression condition = javaParser.getExpressionParser().parseExpression(stack);
        if(stack.trim().peek().getType() != TokenType.GROUP_END)
            throw new UnexpectedTokenException(stack.pop());
        stack.pop();
        MacroList body = parseMacroList(stack, Arrays.asList("ELSE_MACRO", "ELSEIF_MACRO", "ENDIF_MACRO"), true);
        List<IfMacro.Branch> branches = new ArrayList<>();
        branches.add(new IfMacro.Branch(condition, body));
        while (stack.peek().getType() == ELSEIF_MACRO) {
            stack.pop();
            if(stack.trim().peek().getType() != TokenType.GROUP_START)
                throw new UnexpectedTokenException(stack.pop());
            stack.pop();
            condition = javaParser.getExpressionParser().parseExpression(stack);
            if(stack.trim().peek().getType() != TokenType.GROUP_END)
                throw new UnexpectedTokenException(stack.pop());
            stack.pop();
            body = parseMacroList(stack, Arrays.asList("ELSE_MACRO", "ELSEIF_MACRO", "ENDIF_MACRO"), true);
            branches.add(new IfMacro.Branch(condition, body));
        }
        MacroList elseBody = null;
        if(stack.peek().getType() == ELSE_MACRO) {
            stack.pop();
            elseBody = parseMacroList(stack, Arrays.asList("ENDIF_MACRO"), true);
        }
        if(stack.peek().getType() != ENDIF_MACRO)
            throw new UnexpectedTokenException(stack.pop());
        stack.pop();
        return new IfMacro(branches, elseBody);
    }

    private UnlessMacro parseUnlessMacro(TokenStack stack) throws UnexpectedTokenException {
        if(stack.peek().getType() != UNLESS_MACRO)
            throw new UnexpectedTokenException(stack.pop());
        stack.pop();
        if(stack.trim().peek().getType() != TokenType.GROUP_START)
            throw new UnexpectedTokenException(stack.pop());
        stack.pop();
        Expression condition = javaParser.getExpressionParser().parseExpression(stack);
        if(stack.trim().peek().getType() != TokenType.GROUP_END)
            throw new UnexpectedTokenException(stack.pop());
        stack.pop();
        MacroList body = parseMacroList(stack, Arrays.asList("ENDUNLESS_MACRO"), true);
        stack.pop();
        return new UnlessMacro(condition, body);
    }

    private IssetMacro parseIssetMacro(TokenStack stack) throws UnexpectedTokenException {
        if(stack.peek().getType() != ISSET_MACRO)
            throw new UnexpectedTokenException(stack.pop());
        stack.pop();
        if(stack.trim().peek().getType() != TokenType.GROUP_START)
            throw new UnexpectedTokenException(stack.pop());
        stack.pop();
        Expression condition = javaParser.getExpressionParser().parseExpression(stack);
        if(stack.trim().peek().getType() != TokenType.GROUP_END)
            throw new UnexpectedTokenException(stack.pop());
        stack.pop();
        MacroList body = parseMacroList(stack, Arrays.asList("ENDISSET_MACRO"), true);
        stack.pop();
        return new IssetMacro(condition, body);
    }

    private EmptyMacro parseEmptyMacro(TokenStack stack) throws UnexpectedTokenException {
        if(stack.peek().getType() != EMPTY_MACRO)
            throw new UnexpectedTokenException(stack.pop());
        stack.pop();
        if(stack.trim().peek().getType() != TokenType.GROUP_START)
            throw new UnexpectedTokenException(stack.pop());
        stack.pop();
        Expression condition = javaParser.getExpressionParser().parseExpression(stack);
        if(stack.trim().peek().getType() != TokenType.GROUP_END)
            throw new UnexpectedTokenException(stack.pop());
        stack.pop();
        MacroList body = parseMacroList(stack, Arrays.asList("ENDEMPTY_MACRO"), true);
        stack.pop();
        return new EmptyMacro(condition, body);
    }

    private WhileMacro parseWhileMacro(TokenStack stack) throws UnexpectedTokenException {
        if(stack.peek().getType() != WHILE_MACRO)
            throw new UnexpectedTokenException(stack.pop());
        stack.pop();
        if(stack.trim().peek().getType() != TokenType.GROUP_START)
            throw new UnexpectedTokenException(stack.pop());
        stack.pop();
        Expression condition = javaParser.getExpressionParser().parseExpression(stack);
        if(stack.trim().peek().getType() != TokenType.GROUP_END)
            throw new UnexpectedTokenException(stack.pop());
        stack.pop();
        MacroList body = parseMacroList(stack, Arrays.asList("ENDWHILE_MACRO"), true);
        stack.pop();
        return new WhileMacro(condition, body);
    }

    private ForEachMacro parseForEachMacro(TokenStack stack) throws UnexpectedTokenException {
        if(stack.peek().getType() != FOREACH_MACRO)
            throw new UnexpectedTokenException(stack.pop());
        stack.pop();
        if(stack.trim().peek().getType() != TokenType.GROUP_START)
            throw new UnexpectedTokenException(stack.pop());
        stack.pop();
        Variable variable = javaParser.getGeneralParser().parseVariable(stack);
        if(stack.trim().peek().getType() != TokenType.COLON)
            throw new UnexpectedTokenException(stack.pop());
        stack.pop();
        Expression iterable = javaParser.getExpressionParser().parseExpression(stack);
        if(stack.trim().peek().getType() != TokenType.GROUP_END)
            throw new UnexpectedTokenException(stack.pop());
        stack.pop();
        MacroList body = parseMacroList(stack, Arrays.asList("ENDFOREACH_MACRO"), true);
        stack.pop();
        return new ForEachMacro(variable, iterable, body);
    }

    private ForElseMacro parseForElseMacro(TokenStack stack) throws UnexpectedTokenException {
        if(stack.peek().getType() != FOREACH_MACRO)
            throw new UnexpectedTokenException(stack.pop());
        stack.pop();
        if(stack.trim().peek().getType() != TokenType.GROUP_START)
            throw new UnexpectedTokenException(stack.pop());
        stack.pop();
        Variable variable = javaParser.getGeneralParser().parseVariable(stack);
        if(stack.trim().peek().getType() != TokenType.COLON)
            throw new UnexpectedTokenException(stack.pop());
        stack.pop();
        Expression iterable = javaParser.getExpressionParser().parseExpression(stack);
        if(stack.trim().peek().getType() != TokenType.GROUP_END)
            throw new UnexpectedTokenException(stack.pop());
        stack.pop();
        MacroList body = parseMacroList(stack, Arrays.asList("EMPTY_MACRO"), true);
        stack.pop();
        MacroList emptyBody = parseMacroList(stack, Arrays.asList("ENDFORELSE_MACRO"), true);
        stack.pop();
        return new ForElseMacro(variable, iterable, body, emptyBody);
    }

}
