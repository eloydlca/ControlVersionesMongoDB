package org.example.dao;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.example.model.UserFile;
import org.example.model.Version;

import java.util.ArrayList;
import java.util.List;

public class MongoDAO implements IMongoDAO {
    private static final String CONNECTION_STRING = "mongodb://172.16.0.91:27017";
    private static final String DATABASE_NAME = "controlVersion";
    private static final String COLLECTION_NAME = "files";

    private MongoDatabase database;

    public MongoDAO() {
        MongoClient mongoClient = MongoClients.create(CONNECTION_STRING);
        this.database = mongoClient.getDatabase(DATABASE_NAME);

        waitForConnection();
    }

    private void waitForConnection() {
        boolean conectado = false;
        int intentos = 0;
        while (!conectado && intentos < 10) {
            try {
                // Verificar si Mongo está listo
                database.runCommand(new Document("ping", 1));
                conectado = true;
            } catch (Exception e) {
                intentos++;
                System.out.println("Esperando conexión a MongoDB... Intento " + intentos);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {}
            }
        }
        if (!conectado) {
            throw new RuntimeException("No se pudo conectar a MongoDB después de varios intentos.");
        }
    }


    @Override
    public void saveFile(UserFile file) {
        MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);

        Document fileDoc = new Document()
                .append("_id", file.getId())
                .append("fileName", file.getFileName())
                .append("fileExtension", file.getFileExtension());

        // Usar List<Document> en lugar de Document[]
        List<Document> versionsList = new ArrayList<>();
        for (Version version : file.getVersions()) {
            versionsList.add(new Document()
                    .append("version", version.getVersion())
                    .append("versionDescription", version.getVersionDescription())
                    .append("data", version.getData())
            );
        }
        fileDoc.append("versions", versionsList);  // <- Cambio clave

        collection.insertOne(fileDoc);
    }

    @Override
    public void addVersionToFile(ObjectId fileId, Version newVersion) {
        MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);

        // Crear documento de la nueva versión
        Document newVersionDoc = new Document()
                .append("version", newVersion.getVersion())
                .append("versionDescription", newVersion.getVersionDescription())
                .append("data", newVersion.getData());

        // Agregar la nueva versión al array sin
        collection.updateOne(
                Filters.eq("_id", fileId),
                new Document("$push", new Document("versions", newVersionDoc))
        );
    }



    @Override
    public List<UserFile> getAllFiles() {
        MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);
        List<UserFile> filesList = new ArrayList<>();

        for (Document fileDoc : collection.find()) {
            ObjectId id = fileDoc.getObjectId("_id");
            String fileName = fileDoc.getString("fileName");
            String fileExtension = fileDoc.getString("fileExtension");

            // Obtener las versiones
            List<Document> versionsDocs = fileDoc.getList("versions", Document.class);
            Version[] versions = new Version[versionsDocs.size()];
            for (int i = 0; i < versionsDocs.size(); i++) {
                float versionNumber = versionsDocs.get(i).getDouble("version").floatValue();
                String versionDescription = versionsDocs.get(i).getString("versionDescription");
                Binary data = versionsDocs.get(i).get("data", Binary.class);
                versions[i] = new Version(versionNumber, versionDescription, data);
            }

            filesList.add(new UserFile(id, fileName, fileExtension, versions));
        }

        return filesList;
    }

    @Override
    public UserFile getFileById(ObjectId id) {
        MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);
        Document fileDoc = collection.find(Filters.eq("_id", new ObjectId(id.toString()))).first();

        if (fileDoc == null) {
            System.out.println("Archivo no encontrado.");
            return null;
        }

        // Convertir `Document` a `UserFile`
        String fileName = fileDoc.getString("fileName");
        String fileExtension = fileDoc.getString("fileExtension");

        // Obtener las versiones
        List<Document> versionsDocs = fileDoc.getList("versions", Document.class);
        Version[] versions = new Version[versionsDocs.size()];
        for (int i = 0; i < versionsDocs.size(); i++) {
            float versionNumber = versionsDocs.get(i).getDouble("version").floatValue();
            String versionDescription = versionsDocs.get(i).getString("versionDescription");
            Binary data = versionsDocs.get(i).get("data", Binary.class);
            versions[i] = new Version(versionNumber, versionDescription, data);
        }

        return new UserFile(id, fileName, fileExtension, versions);
    }

    @Override
    public UserFile getFileByFileNameAndExtension(String fileName, String fileExtension) {
        MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);

        // Crear un filtro para verificar si existe un archivo con el nombre y la extensión proporcionados
        Document filtro = new Document("fileName", fileName)
                .append("fileExtension", fileExtension);

        // Intentamos encontrar el primer archivo que coincida con el filtro
        Document archivoDoc = collection.find(filtro).first();

        // Si no encontramos el archivo, retorna null. Si lo encontramos, lo convierte en un objeto UserFile
        if (archivoDoc == null) {
            return null;
        }

        // Convertir `Document` a `UserFile`
        ObjectId id = archivoDoc.getObjectId("_id");
        String fileNameResult = archivoDoc.getString("fileName");
        String fileExtensionResult = archivoDoc.getString("fileExtension");

        // Obtener las versiones
        List<Document> versionsDocs = archivoDoc.getList("versions", Document.class);
        Version[] versions = new Version[versionsDocs.size()];
        for (int i = 0; i < versionsDocs.size(); i++) {
            float versionNumber = versionsDocs.get(i).getDouble("version").floatValue();
            String versionDescription = versionsDocs.get(i).getString("versionDescription");
            Binary data = versionsDocs.get(i).get("data", Binary.class);
            versions[i] = new Version(versionNumber, versionDescription, data);
        }

        return new UserFile(id, fileNameResult, fileExtensionResult, versions);
    }

    @Override
    public void updateFile(UserFile file) {
        MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);

        // Corregido: Usar List<Document>
        List<Document> versionsList = new ArrayList<>();
        for (Version version : file.getVersions()) {
            versionsList.add(new Document()
                    .append("version", version.getVersion())
                    .append("versionDescription", version.getVersionDescription())
                    .append("data", version.getData())
            );
        }

        Document updatedData = new Document()
                .append("fileName", file.getFileName())
                .append("fileExtension", file.getFileExtension())
                .append("versions", versionsList);  // <- Cambio clave

        collection.updateOne(
                Filters.eq("_id", file.getId()),
                new Document("$set", updatedData)
        );
    }

    @Override
    public long deleteFile(ObjectId id) {
        MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);

        // Eliminar el documento por ID
        DeleteResult result = collection.deleteOne(Filters.eq("_id", id));

        return result.getDeletedCount();
    }

    @Override
    public void removeVersionFromFile(ObjectId fileId, float versionNumber) {
        MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);

        // Usar `$pull` para eliminar la versión específica
        collection.updateOne(
                Filters.eq("_id", fileId),
                new Document("$pull", new Document("versions", new Document("version", versionNumber)))
        );
    }


}
