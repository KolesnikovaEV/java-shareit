package ru.practicum.gate.item;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.gate.client.BaseClient;
import ru.practicum.gate.item.dto.CreateCommentDto;
import ru.practicum.gate.item.dto.CreateUpdateItemDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createItem(Long ownerId, CreateUpdateItemDto createUpdateItemDto) {
        return post("", ownerId, createUpdateItemDto);
    }

    public ResponseEntity<Object> getAllItems(Long ownerId) {
        return get("/", ownerId);
    }

    public ResponseEntity<Object> getItemById(Long ownerId, Long itemId) {
        return get("/" + itemId, ownerId);
    }

    public ResponseEntity<Object> updateItem(long ownerId, long itemId, CreateUpdateItemDto createUpdateItemDto) {
        return patch("/" + itemId, ownerId, createUpdateItemDto);
    }

    public ResponseEntity<Object> searchItem(Long userId, String text) {
        Map<String, Object> parameters = Map.of(
                "text", text
        );
        return get("/search?text={text}", userId, parameters);
    }

    public ResponseEntity<Object> createComment(Long itemId, Long userId, CreateCommentDto commentDto) {
        return post("/" + userId + "/comment", itemId, commentDto);
    }

    public void deleteItem(Long userId, Long itemId) {
        delete("/" + userId, itemId);
    }
}
