package org.baeldung.persistence.model;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
public class DeviceMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long userId;
    private String deviceDetails;
    private String location;
    private Date lastLoggedIn;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getDeviceDetails() {
        return deviceDetails;
    }

    public void setDeviceDetails(String deviceDetails) {
        this.deviceDetails = deviceDetails;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getLastLoggedIn() {
        return lastLoggedIn;
    }

    public void setLastLoggedIn(Date lastLoggedIn) {
        this.lastLoggedIn = lastLoggedIn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceMetadata that = (DeviceMetadata) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(deviceDetails, that.deviceDetails) &&
                Objects.equals(location, that.location) &&
                Objects.equals(lastLoggedIn, that.lastLoggedIn);
    }
/*   La méthode "public int hashCode()" est souvent implémentée en conjonction avec
 *  la méthode "public boolean equals(Object obj)" pour garantir une égalité correcte 
 *  des objets dansles structures de données qui utilisent l'algorithme de hachage.*/
    @Override
    public int hashCode() {
        return Objects.hash(id, userId, deviceDetails, location, lastLoggedIn);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DeviceMetadata{");/* la méthode "toString()" est utilisée pour renvoyer une représentation
         textuelle d'un objet "DeviceMetadata". La méthode utilise un objet "StringBuilder"
          pour construire la chaîne de caractères représentant l'objet.
           La chaîne de caractères contiendra les valeurs des propriétés de l'objet, 
           séparées par des virgules et des espaces, et sera encadrée par des accolades. */
        sb.append("id=").append(id);//, cette instruction ajoute une chaîne de caractères à
        //l'objet StringBuilder "sb" représentant la propriété "id" de
        //l'objet courant, avec un préfixe "id="
        sb.append(", userId=").append(userId);
        sb.append(", deviceDetails='").append(deviceDetails).append('\'');
        sb.append(", location='").append(location).append('\'');
        sb.append(", lastLoggedIn=").append(lastLoggedIn);
        sb.append('}');
        return sb.toString();
    }
}
