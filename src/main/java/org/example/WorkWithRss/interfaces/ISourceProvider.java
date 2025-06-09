package org.example.WorkWithRss.interfaces;

import java.util.List;
import org.example.Storage.Source;

public interface ISourceProvider {
    List<Source> getSources();
}