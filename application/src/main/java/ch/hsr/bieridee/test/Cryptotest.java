package ch.hsr.bieridee.test;

import org.junit.Test;

import junit.framework.Assert;
import ch.hsr.bieridee.util.Hashhelper;

public class Cryptotest {

	@Test
	public void testSHA1genaration() {
		final String value = "iwantbeer";
		final String iwantbeersha1 = "7649878cc87b7bf2231a8256714e3e3972e4002b";
		Assert.assertEquals(iwantbeersha1, Hashhelper.encryptPassword(value));
	}

}
