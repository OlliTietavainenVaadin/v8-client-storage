package org.vaadin.olli.demo;

import javax.servlet.annotation.WebServlet;

import org.vaadin.olli.ClientStorage;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.HasValue;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("demo")
@Title("ClientStorage add-on demo")
@SuppressWarnings("serial")
public class DemoUI extends UI {

    private static final String KEY = "EXAMPLE_KEY";
    private ClientStorage clientStorage;
    private int ignoreValueChange = 0;
    private TextField sessionStorageField;
    private TextField localStorageField;

    @Override
    protected void init(VaadinRequest request) {
        final VerticalLayout vlayout = new VerticalLayout();
        vlayout.setMargin(true);
        setContent(vlayout);

        final Label label = new Label("The following is stored into, and " + "retreived from, HTML5 storage");
        vlayout.addComponent(label);

        final FormLayout layout = new FormLayout();
        vlayout.addComponent(layout);

        localStorageField = new TextField("Local Storage");
        localStorageField.setPlaceholder("No value stored");
        localStorageField.addValueChangeListener(event -> {
            if (ignoreValueChange == 0) {
                clientStorage.setLocalItem(KEY, localStorageField.getValue());
            }
        });
        layout.addComponent(localStorageField);
        layout.addComponent(new Button("Clear Local Storage", new Button.ClickListener() {
            @Override
            public void buttonClick(final Button.ClickEvent event) {
                clientStorage.removeLocalItem(KEY);
                updateClientStorage();
            }
        }));

        sessionStorageField = new TextField("Session Storage");

        sessionStorageField.setPlaceholder("No value stored");
        sessionStorageField.addValueChangeListener(event -> {
            if (ignoreValueChange == 0) {
                clientStorage.setSessionItem(KEY, sessionStorageField.getValue());
            }
        });
        layout.addComponent(sessionStorageField);
        layout.addComponent(new Button("Clear Session Storage", new Button.ClickListener() {
            @Override
            public void buttonClick(final Button.ClickEvent event) {
                clientStorage.removeSessionItem(KEY);
                updateClientStorage();
            }
        }));

        vlayout.addComponent(new Link("More information about HTML5 storage",
            new ExternalResource("https://developers.google.com" + "/web-toolkit/doc/latest/" + "DevGuideHtml5Storage#GwtStorage")));

        clientStorage = new ClientStorage(new ClientStorage.ClientStorageSupportListener() {
            @Override
            public void clientStorageIsSupported(final boolean supported) {
                if (!supported) {
                    layout.removeComponent(localStorageField);
                    layout.removeComponent(sessionStorageField);
                    layout.addComponent(new Label("This browser doesn't support HTML5 storage"));
                }
            }
        });

        addExtension(clientStorage);

        updateClientStorage();
        updateSessionStorage();
    }

    private void updateSessionStorage() {
        clientStorage.getSessionItem(KEY, value -> {
            ignoreValueChange++;
            sessionStorageField.setValue(value == null ? "" : value);
            ignoreValueChange--;
        });
    }

    private void updateClientStorage() {
        clientStorage.getLocalItem(KEY, value -> {
            ignoreValueChange++;
            localStorageField.setValue(value == null ? "" : value);
            ignoreValueChange--;
        });
    }

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = DemoUI.class)
    public static class Servlet extends VaadinServlet {
    }
}
