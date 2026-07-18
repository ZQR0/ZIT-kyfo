package zit.kyfo.backend.dao.entity;

//Теперь будет использоваться для сущностей вместо
// * {@link BaseEntity} и {@link Entity}

import jakarta.persistence.*;
import lombok.Getter;
import java.io.Serializable;

@MappedSuperclass
@Getter
public abstract class AbstractEntity<ID extends Serializable> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    protected ID id;

    protected void setId(ID id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        this.id = id;
    }
}
