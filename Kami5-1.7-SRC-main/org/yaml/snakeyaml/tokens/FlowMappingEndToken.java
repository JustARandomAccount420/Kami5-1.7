/*
 * Decompiled with CFR 0.151.
 */
package org.yaml.snakeyaml.tokens;

import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.tokens.Token;

public final class FlowMappingEndToken
extends Token {
    public FlowMappingEndToken(Mark startMark, Mark endMark) {
        super(startMark, endMark);
    }

    @Override
    public Token.ID getTokenId() {
        return Token.ID.FlowMappingEnd;
    }
}

