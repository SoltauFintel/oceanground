package de.mwvb.oceanground.docker;

import java.util.Map;

import org.pmw.tinylog.Logger;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.api.model.Ports.Binding;

public class PortsBuilder {
	private String ports;
	private Integer publicPort;

	public PortsBuilder(InspectContainerResponse c) {
		try {
			ports = "";
			if (c.getConfig().getPortSpecs() != null) {
				for (String p : c.getConfig().getPortSpecs()) {
					if (!ports.isEmpty()) {
						ports += " | ";
					}
					ports += p;
				}
			}
			
			Ports portBindings = c.getHostConfig().getPortBindings();
			if (portBindings != null) {
				for (Map.Entry<ExposedPort, Binding[]> e : portBindings.getBindings().entrySet()) {
					int containerPort = e.getKey().getPort();
					String host = "";
					if (e.getValue() != null && e.getValue().length == 1) {
						host = e.getValue()[0].getHostIp();
						if (host == null) {
							host = e.getValue()[0].getHostPortSpec();
							try {
								publicPort = Integer.parseInt(host);
							} catch (NumberFormatException ignore) {
							}
						} else {
							host += ":" + e.getValue()[0].getHostPortSpec();
						}
					}
					if (!ports.isEmpty()) {
						ports += " | ";
					}
					ports += "Host=" + host + " -> Container=" + e.getKey().getProtocol() + ":" + containerPort; 
				}
			}
			
			if (ports.isEmpty()) {
				ports = "-";
			}
		} catch (Exception e) {
			Logger.error(e);
			ports = "error";
		}
	}
	
	public String getPorts() {
		return ports;
	}

	/**
	 * @return can be null
	 */
	public Integer getPublicPort() {
		return publicPort;
	}
	
	public static String buildPortString(Container c) {
		String p = "";
		for (int i = 0; i < c.getPorts().length; i++) {
			if (c.getPorts()[i].getPublicPort() == null) {
				if (c.getPorts()[i].getPrivatePort() != null) {
					if (!p.isEmpty()) {
						p += ", ";
					}
					p += "[" + c.getPorts()[i].getPrivatePort() + "]";
				}
			} else {
				if (!p.isEmpty()) {
					p += ", ";
				}
				p += c.getPorts()[i].getPublicPort();
			}
		}
		return p.isEmpty() ? "-" : p;
	}
}
