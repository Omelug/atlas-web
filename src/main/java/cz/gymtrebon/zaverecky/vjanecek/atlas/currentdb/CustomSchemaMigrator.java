package cz.gymtrebon.zaverecky.vjanecek.atlas.currentdb;

import org.hibernate.boot.Metadata;
import org.hibernate.tool.schema.spi.ExecutionOptions;
import org.hibernate.tool.schema.spi.SchemaMigrator;
import org.hibernate.tool.schema.spi.TargetDescriptor;

public class CustomSchemaMigrator implements SchemaMigrator {
    @Override
    public void doMigration(Metadata metadata, ExecutionOptions options, TargetDescriptor targetDescriptor) {
    }
}