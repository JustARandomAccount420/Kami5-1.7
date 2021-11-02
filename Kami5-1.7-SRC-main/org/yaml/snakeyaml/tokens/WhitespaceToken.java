/*
 * Decompiled with CFR 0.151.
 */
package org.yaml.snakeyaml.tokens;

import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.tokens.Token;

public class WhitespaceToken
extends Token {
    public WhitespaceToken(Mark startMark, Mark endMark) {
        super(startMark, endMark);
    }

    @Override
    public Token.ID getTokenId() {
        return Token.ID.Whitespace;
    }
}

