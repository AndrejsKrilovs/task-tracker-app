
package krilovs.andrejs.app.mapper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import krilovs.andrejs.app.entity.TaskStatus;
import krilovs.andrejs.app.mapper.task.TaskStatusDeserializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TaskStatusDeserializerTest {

  private TaskStatusDeserializer deserializer;
  private JsonParser jsonParser;
  private DeserializationContext context;

  @BeforeEach
  void setUp() {
    deserializer = new TaskStatusDeserializer();
    jsonParser = mock(JsonParser.class);
    context = mock(DeserializationContext.class);
  }

  @Test
  void shouldDeserializeValidStatus() throws IOException {
    when(jsonParser.getText()).thenReturn("in_development");

    TaskStatus result = deserializer.deserialize(jsonParser, context);

    assertEquals(TaskStatus.IN_DEVELOPMENT, result);
  }

  @Test
  void shouldReturnNullForEmptyString() throws IOException {
    when(jsonParser.getText()).thenReturn("");

    TaskStatus result = deserializer.deserialize(jsonParser, context);

    assertNull(result);
  }

  @Test
  void shouldReturnNullForNullInput() throws IOException {
    when(jsonParser.getText()).thenReturn(null);

    TaskStatus result = deserializer.deserialize(jsonParser, context);

    assertNull(result);
  }

  @Test
  void shouldReturnNullForInvalidValue() throws IOException {
    when(jsonParser.getText()).thenReturn("invalid_status");

    TaskStatus result = deserializer.deserialize(jsonParser, context);

    assertNull(result);
  }
}
