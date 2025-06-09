package org.example.console;

import java.util.List;

public record Page<T>(List<T> items, int pageNo, int pagesTotal) { }

