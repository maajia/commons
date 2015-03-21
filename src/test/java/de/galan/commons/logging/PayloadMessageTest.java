package de.galan.commons.logging;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

import de.galan.commons.test.AbstractTestParent;


/**
 * CUT PayloadMessage
 *
 * @author daniel
 */
public class PayloadMessageTest extends AbstractTestParent {

	protected void assertMsg(String pattern, Object[] args, boolean identifier, Object[] argsExpected, Throwable throwableExpected, String messageExpected) {
		PayloadMessage msg = new PayloadMessage(pattern, args, identifier);
		assertThat(msg.getFormat()).isEqualTo(pattern);
		assertThat(msg.getParameters()).containsExactly(argsExpected);
		assertThat(msg.getThrowable()).isEqualTo(throwableExpected);
		assertThat(msg.getFormattedMessage()).isEqualTo(messageExpected);
	}


	@Test
	public void simple() throws Exception {
		assertMsg("Hello world", args(), false, args(), null, "Hello world");
		assertMsg("Hello world", args(), true, args(), null, "Hello world");
	}


	@Test
	public void parameter() throws Exception {
		assertMsg("x {} y {}", args("a", 1), false, args("a", 1), null, "x {a} y {1}");
		assertMsg("x {} y {}", args("a", 1), true, args("a", 1), null, "x {0:a} y {1:1}");
	}


	@Test
	public void parameterNames() throws Exception {
		assertMsg("Hello {first} world {second}", args("a", 1), false, args("a", 1), null, "Hello {a} world {1}");
		assertMsg("Hello {first} world {second}", args("a", 1), true, args("a", 1), null, "Hello {first:a} world {second:1}");
	}


	@Test
	public void parameterNextToEachOther() throws Exception {
		assertMsg("{first}{second}{third}", args("a", 1, true), false, args("a", 1, true), null, "{a}{1}{true}");
		assertMsg("{first}{second}{third}", args("a", 1, true), true, args("a", 1, true), null, "{first:a}{second:1}{third:true}");
	}


	@Test
	public void parameterNameSparse() throws Exception {
		assertMsg("{}x{second}y{}", args("a", 1, true), false, args("a", 1, true), null, "{a}x{1}y{true}");
		assertMsg("{}x{second}y{}", args("a", 1, true), true, args("a", 1, true), null, "{0:a}x{second:1}y{1:true}");
	}


	@Test
	public void unclosedCurlyBrackets() throws Exception {
		assertMsg("x {a} y {b", args("a", 1), false, args("a", 1), null, "Invalid pattern, curly brace left unclosed.");
		assertMsg("x {a} y {b", args("a", 1), true, args("a", 1), null, "Invalid pattern, curly brace left unclosed.");
	}


	@Test
	public void missingArgument() throws Exception {
		assertMsg("Hello {} world {}, {x}!", args("a"), false, args("a"), null, "Invalid amount of arguments (only 1 available, 2 missing)");
		assertMsg("Hello {} world {}, {x}!", args("a"), true, args("a"), null, "Invalid amount of arguments (only 1 available, 2 missing)");
	}


	@Test
	public void parameterNamesThrowable() throws Exception {
		NullPointerException exception = new NullPointerException("BAM");
		assertMsg("Hello {first} world {second}", args("a", 1, exception), false, args("a", 1), exception, "Hello {a} world {1}");
		assertMsg("Hello {first} world {second}", args("a", 1, exception), true, args("a", 1), exception, "Hello {first:a} world {second:1}");
	}


	@Test
	public void parameterNamesToMuch() throws Exception {
		assertMsg("{first} x {second}", args("a", 1, 4), false, args("a", 1, 4), null, "Invalid amount of arguments (3 given but only 2 used in pattern)");
		assertMsg("{first} x {second}", args("a", 1, 4), true, args("a", 1, 4), null, "Invalid amount of arguments (3 given but only 2 used in pattern)");
	}


	@Test
	public void parameterNamesToMuchWithThrowable() throws Exception {
		NullPointerException ex = new NullPointerException("BAM");
		assertMsg("{a} x {b}", args("a", 1, 4, ex), false, args("a", 1, 4, ex), null, "Invalid amount of arguments (4 given but only 2 used in pattern)");
		assertMsg("{a} x {b}", args("a", 1, 4, ex), true, args("a", 1, 4, ex), null, "Invalid amount of arguments (4 given but only 2 used in pattern)");
	}


	// Deficit of Log4j not enforcing separation  of arguments and throwable
	@Test
	public void parameterNamesIsThrowable() throws Exception {
		NullPointerException ex = new NullPointerException("BAM");
		assertMsg("{ex} Lee", args(ex), false, args(ex), null, "{java.lang.NullPointerException: BAM} Lee");
		assertMsg("{ex} Lee", args(ex), true, args(ex), null, "{ex:java.lang.NullPointerException: BAM} Lee");
	}


	protected Object[] args(Object... arguments) {
		return arguments;
	}

}