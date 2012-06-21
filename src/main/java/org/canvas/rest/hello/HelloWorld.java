package org.canvas.rest.hello;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class HelloWorld {

    private String greeting;
    private String locale;

    public String getGreeting() {
        return greeting;
    }

    public void setGreeting(String greeting) {
        this.greeting = greeting;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String lang) {
        this.locale = lang;
    }
}
