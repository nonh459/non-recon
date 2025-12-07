package com.nontiwa.nonrecon.simpleetl;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.batch.item.ResourceAware;
import org.springframework.core.io.Resource;

@Entity
@Table(name = "customer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer implements ResourceAware {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name="last_name")
    private String lastName;

    private String email;

    private int age;

    private String sourceFile;

    @Transient
    private Resource resource;

    @Override
    public void setResource(@NonNull Resource resource) {
        this.resource = resource;
    }
}
