package com.twohundredone.taskonserver.chat.entity;

import com.twohundredone.taskonserver.common.entity.BaseEntity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "chat_user",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_chat_user", columnNames = {"chat_id", "user_id"})
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChatUser extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_user_id")
    private Long chatUserId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", nullable = false)
    private ChatRoom chatRoom;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "last_read_at", nullable = false)
    private LocalDateTime lastReadAt;

    public void updateLastReadAt(LocalDateTime time) {
        this.lastReadAt = time;
    }
}
