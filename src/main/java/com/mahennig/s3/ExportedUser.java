package com.mahennig.s3;

/** One user as written to the export file. */
public record ExportedUser(Long id, String name, String email) {
}
