package guru.qa.niffler.db.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Data
public class AuthorityEntity {
    private UUID id;
    private Authority authority;
}
