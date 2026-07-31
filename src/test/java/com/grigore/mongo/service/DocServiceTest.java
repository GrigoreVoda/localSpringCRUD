package com.grigore.mongo.service;

import com.grigore.mongo.exception.UserNotFoundException;
import com.grigore.mongo.model.Doc;
import com.grigore.mongo.repository.DocsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocServiceTest {

    @Mock
    private DocsRepository docsRepository;

    private DocService docService;

    @BeforeEach
    void setUp() {
        docService = new DocService(docsRepository);
    }

    private Doc doc(String id, String type, LocalDate expireDate) {
        Doc d = new Doc();
        d.setId(id);
        d.setDocType(type);
        d.setDocNumber(id + "-num");
        d.setExpireDate(expireDate);
        return d;
    }

    @Test
    void findAllDocsSortsBySoonestExpiryFirst() {
        Doc expiresLast = doc("1", "Passport", LocalDate.of(2027, 1, 1));
        Doc expiresFirst = doc("2", "ID card", LocalDate.of(2026, 6, 1));
        Doc expiresMiddle = doc("3", "License", LocalDate.of(2026, 10, 1));
        when(docsRepository.findAll()).thenReturn(List.of(expiresLast, expiresFirst, expiresMiddle));

        List<Doc> sorted = docService.findAllDocs();

        assertEquals(List.of(expiresFirst, expiresMiddle, expiresLast), sorted);
    }

    @Test
    void findDocByIdThrowsWhenMissing() {
        when(docsRepository.findDocsById("missing")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> docService.findDocById("missing"));
    }

    @Test
    void deleteDocIsNoOpWhenDocDoesNotExist() {
        when(docsRepository.findDocsById("missing")).thenReturn(Optional.empty());

        docService.deleteDoc("missing");

        verify(docsRepository, never()).deleteById(anyString());
    }
}
