package ru.practicum.shareit.item.comment;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CommentDtoMapper {
    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .created(comment.getCreated())
                .authorName(comment.getAuthor().getName())
                .build();
    }

    public static Comment toCommentFromCreateCommentDto(CreateCommentDto commentDto) {
        return Comment.builder()
                .text(commentDto.getText())
                .build();
    }
}
