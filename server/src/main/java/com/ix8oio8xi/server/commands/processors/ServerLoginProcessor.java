package com.ix8oio8xi.server.commands.processors;

import com.ix8oio8xi.commands.CommandProcessor;
import com.ix8oio8xi.data.Users;
import com.ix8oio8xi.messages.MessageBuilder;
import com.ix8oio8xi.messages.MessageDecoder;
import com.ix8oio8xi.server.commands.ServerProcessor;
import com.ix8oio8xi.server.HandlerState;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 * Login processor for server. Processor expect to receive message in following format:
 * <p>
 * +-------------------+----------------------+
 * |       Login       |       Password       |
 * +-------------------+----------------------+
 * |   string          |   string             |
 * +-------------------+----------------------+
 * <p>
 * After processing, the processor sends the response in the format:
 * +---------------------+------------------+
 * |       Success       |       Info       |
 * +---------------------+------------------+
 * |   byte              |   string         |
 * +---------------------+------------------+
 */
@CommandProcessor(opCode = 0x1)
public class ServerLoginProcessor implements ServerProcessor {
    /**
     * Checks the correctness of the login, the number of remaining login attempts,
     * and the correctness of the password. If everything is in order, registers
     * the user as logged into the system. Otherwise, sends an explanatory message
     * with success = false.
     */
    @Override
    public void process(ByteBuf message, HandlerState state, ChannelHandlerContext ctx) {
        String login = MessageDecoder.decodeString(message);
        String password = MessageDecoder.decodeString(message);

        MessageBuilder messageBuilder = new MessageBuilder(opCode());

        int currAttempt = state.attemptsLeft();

        if (state.isLoggedIn()) {
            messageBuilder.addBoolean(true).addString("You are already logged in.");
        } else if (!Users.containsUser(login)) {
            messageBuilder.addBoolean(false).addString("There is no user with such login, please try again.");
        } else if (currAttempt <= 0) {
            messageBuilder.addBoolean(false).addString("You have run out of attempts.");
        } else if (!Users.checkPassword(login, password)) {
            messageBuilder.addBoolean(false).addString("Incorrect password, please try again.");
        } else {
            state.login();
            messageBuilder.addBoolean(true).addString("You have logged in!");
        }

        ctx.channel().writeAndFlush(messageBuilder.build());
    }

    private static byte opCode() {
        return 0x1;
    }
}
