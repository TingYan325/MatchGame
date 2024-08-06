package com.zy.matchgame.config;
 
import jakarta.servlet.ServletRequestEvent;
import jakarta.servlet.ServletRequestListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
 
@Component
public class WebsocketListener implements ServletRequestListener {
    @Override
    public void requestInitialized(ServletRequestEvent sre)  {
        HttpSession session = ((HttpServletRequest) sre.getServletRequest()).getSession();
    }
 
    public WebsocketListener(){}
 
    @Override
    public void requestDestroyed(ServletRequestEvent arg0)  {}
}