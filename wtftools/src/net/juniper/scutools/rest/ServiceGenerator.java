package net.juniper.scutools.rest;

import java.io.IOException;

public interface ServiceGenerator {
	public String run() throws IOException;
	public boolean exist();
}
