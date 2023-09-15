package com.felix;

import java.util.ArrayList;
import java.util.List;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import java.util.regex.Pattern;
import java.util.stream.Collectors;


public final class FileSearchUtils {

    private FileSearchUtils() {}

    private static final int CHUNK_SIZE = 1024 * 1024 * 10;

    public static List<String> searchFile(
        final String filePath,
        final List<String> keywordList,
        final int resultSize) {
        
        List<String> result = new ArrayList<>();

        // get the regex for matching all the keywords in a String
        // reverse the words since we will be reading backwards from the end of file
        final String regexString = keywordList.stream()
            .map(word -> new StringBuilder(word).reverse())
            .map(reversedWord -> "(?=.*" + reversedWord + ")")
            .collect(Collectors.joining(""));

        final Pattern pattern = regexString.length() > 0 ? Pattern.compile(regexString) : null;
        
        try (RandomAccessFile raf = new RandomAccessFile(filePath, "r");
            FileChannel channel = raf.getChannel()) {

            // read the file backwards, so we get the latest log events first
            long fileLength = channel.size();
            long filePointer = fileLength - 1;

            StringBuilder currentLine = new StringBuilder();

            fileReadLoop:
            while (filePointer >= 0) {

                long chunkStart = Math.max(filePointer - CHUNK_SIZE + 1, 0);
                long chunkSizeToRead = filePointer - chunkStart + 1;
                MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, chunkStart, chunkSizeToRead);

                // reading backwards
                for (long i = chunkSizeToRead - 1; i >= 0; i--) {
                    byte currentByte = buffer.get((int) i);

                    if (currentByte == '\r') {
                        continue;
                    }

                    // just read a line
                    if (currentByte == '\n') {
                        if (pattern == null || pattern.matcher(currentLine).find()) {
                            if (currentLine.length() > 0) {
                                result.add(currentLine.reverse().toString());
                            }
                            
                            if (result.size() >= resultSize) {
                                break fileReadLoop;
                            }
                        }
                        
                        currentLine.setLength(0);
                    } else {
                        currentLine.append((char) currentByte);
                    }

                }
                
                // go to the next chunk
                filePointer -= CHUNK_SIZE;
            }

            if (currentLine.length() > 0 && result.size() < resultSize) {
                if (pattern == null || pattern.matcher(currentLine).find()) {
                    if (currentLine.length() > 0) {
                        result.add(currentLine.reverse().toString());
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();

        }

        return result;
    }
}