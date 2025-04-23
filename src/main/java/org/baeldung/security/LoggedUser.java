package org.baeldung.security;

import java.util.List;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.springframework.stereotype.Component;

@Component
public class LoggedUser implements HttpSessionBindingListener {

    private String username;
    private ActiveUserStore activeUserStore;

    public LoggedUser(String username, ActiveUserStore activeUserStore) {
        this.username = username;
        this.activeUserStore = activeUserStore;
    }

    public LoggedUser() {
    }

    @Override
    public void valueBound(HttpSessionBindingEvent event) {
        List<String> users = activeUserStore.getUsers();
        LoggedUser user = (LoggedUser) event.getValue();
        if (!users.contains(user.getUsername())) {
            users.add(user.getUsername());
        }
    }

    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {
        if (event == null || event.getValue() == null) {
            System.out.println("Event ou event.getValue() est null dans valueUnbound");
            return;
        }

        try {
            List<String> users = activeUserStore.getUsers();
            if (users == null) {
                System.out.println("La liste des utilisateurs actifs est null");
                return;
            }

            LoggedUser user = (LoggedUser) event.getValue();
            if (user == null) {
                System.out.println("L'utilisateur connecté est null");
                return;
            }

            String username = user.getUsername();
            if (username == null) {
                System.out.println("Le nom d'utilisateur est null");
                return;
            }

            if (users.contains(username)) {
                users.remove(username);
                System.out.println("Utilisateur " + username + " supprimé de la liste des utilisateurs actifs");
            } else {
                System.out.println("L'utilisateur " + username + " n'est pas dans la liste des utilisateurs actifs");
            }
        } catch (Exception e) {
            System.err.println("Exception dans valueUnbound: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
