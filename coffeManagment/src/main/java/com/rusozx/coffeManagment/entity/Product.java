package com.rusozx.coffeManagment.entity;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;


@NamedQuery(name="Product.updateStatus",
        query = "update Product p set p.status= :status where p.id = :id")

@Data
@Entity
@DynamicUpdate
@DynamicInsert
@Table(name="product")
public class Product implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;

    @Column(name="name")
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="category_fk", nullable=false)
    private Category category;

    @Column(name="description")
    private String description;

    @Column(name="price")
    private Integer price;

    @Column(name="status")
    private String status;

}
