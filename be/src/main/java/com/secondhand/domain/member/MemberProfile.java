package com.secondhand.domain.member;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class MemberProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_profile_id")
    private Long id;

    private String memberEmail;

    @OneToOne(mappedBy = "memberProfile")
    private Member member;

    public MemberProfile(String memberEmail) {
        this.memberEmail = memberEmail;
    }

    public void setEmail(String email) {
        this.memberEmail = email;
    }

}
