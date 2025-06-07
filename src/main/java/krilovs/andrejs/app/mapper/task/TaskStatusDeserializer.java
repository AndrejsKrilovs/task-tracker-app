package krilovs.andrejs.app.mapper.task;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import krilovs.andrejs.app.entity.TaskStatus;

import java.io.IOException;

public class TaskStatusDeserializer extends JsonDeserializer<TaskStatus> {
  @Override
  public TaskStatus deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
    String value = jsonParser.getText();
    if (value == null || value.trim().isEmpty()) {
      return null;
    }
    try {
      return TaskStatus.valueOf(value.trim().toUpperCase());
    } catch (IllegalArgumentException ex) {
      return null;
    }
  }
}
