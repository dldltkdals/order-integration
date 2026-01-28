package com.inspien.order_integration.policy;

public interface IdGenerator {
    String generateNextId();
    void initialize(String lastId);
}
