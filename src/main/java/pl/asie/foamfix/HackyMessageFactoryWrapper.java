package pl.asie.foamfix;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;

import java.lang.reflect.Field;

public class HackyMessageFactoryWrapper implements MessageFactory {
	private final MessageFactory delegate;

	public HackyMessageFactoryWrapper(MessageFactory delegate) {
		this.delegate = delegate;
	}

	public static Logger fixLogger(Logger logger) {
		if (logger instanceof org.apache.logging.log4j.spi.AbstractLogger) {
			try {
				Field f = org.apache.logging.log4j.spi.AbstractLogger.class.getDeclaredField("messageFactory");
				f.setAccessible(true);
				MessageFactory mf = (MessageFactory) f.get(logger);
				if (!(mf instanceof HackyMessageFactoryWrapper)) {
					f.set(logger, new HackyMessageFactoryWrapper(mf));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return logger;
	}

	public void test() {
		Logger logger = fixLogger(LogManager.getLogger());
	}

	@Override
	public Message newMessage(Object message) {
		if (message instanceof String) {
			message = ((String) message).replaceAll("\\$\\{", "{");
		}
		return this.delegate.newMessage(message);
	}

	@Override
	public Message newMessage(String message) {
		if (message != null) {
			message = message.replaceAll("\\$\\{", "{");
		}
		return this.delegate.newMessage(message);
	}

	@Override
	public Message newMessage(String message, Object... params) {
		if (message != null) {
			message = message.replaceAll("\\$\\{", "{");
		}
		for (int i = 0; i < params.length; i++) {
			if (params[i] instanceof String) {
				params[i] = ((String) params[i]).replaceAll("\\$\\{", "{");
			}
		}
		return this.delegate.newMessage(message, params);
	}
}
