package org.vaadin.olli.shared;

import com.vaadin.shared.communication.ServerRpc;

public interface ClientStorageServerRpc extends ServerRpc {
	void setSupport(boolean supported);

	void returnValue(String uuid, String value);
}
