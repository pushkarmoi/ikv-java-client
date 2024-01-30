package io.inlined;

import io.inlined.clients.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NearlineIntegrationTests {
  private final ClientOptions _clientOptions =
      new ClientOptions.Builder()
          .withMountDirectory("/tmp/NearlineIntegrationTests")
          .withStoreName("testing-store")
          .withAccountId("testing-account-v1")
          .withAccountPassKey("testing-account-passkey")
          .useStringPrimaryKey()
          .build();

  @Test
  public void upsertAndRead() throws InterruptedException {
    IKVClientFactory factory = new IKVClientFactory(_clientOptions);

    /*InlineKVWriter writer = factory.createNewWriterInstance();

    writer.startupWriter();

    IKVDocument document =
        new IKVDocument.Builder()
            .putStringField("userid", "id_2") // primary key
                .putIntField("age", 25)
                .putLongField("ageAsLong", 25)
                .putFloatField("ageAsFloat", 25.2f)
                .putDoubleField("ageAsDouble", 25.2)
                .putStringField("firstname", "Alice")
            .build();
    writer.upsertFieldValues(document);

    Thread.sleep(1000);*/

    InlineKVReader reader = factory.createNewReaderInstance();
    reader.startupReader();

    String userid = reader.getStringValue("id_2", "userid");
    Assertions.assertEquals(userid, "id_2");

    Assertions.assertEquals(reader.getIntValue("id_2", "age"), 25);
    Assertions.assertEquals(reader.getLongValue("id_2", "ageAsLong"), 25);
    Assertions.assertEquals(reader.getFloatValue("id_2", "ageAsFloat"), 25.2f);
    Assertions.assertEquals(reader.getDoubleValue("id_2", "ageAsDouble"), 25.2);

    String firstName = reader.getStringValue("id_2", "firstname");
    Assertions.assertEquals(firstName, "Alice");

    reader.shutdownReader();
  }
}
