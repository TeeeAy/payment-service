package backend.template.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.UUID;

@SuppressWarnings("LombokDataInspection")
@Entity
@Table
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@SuperBuilder(setterPrefix = "with", toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public abstract class Payment {

    public abstract String getType();

    @Id
    @JsonIgnore
    protected String id;

    @Column
    @Enumerated(EnumType.STRING)
    @Builder.Default
    protected Status status = Status.PENDING;

    @PrePersist
    private void initialize() {
        id = UUID.randomUUID().toString();
    }

}
