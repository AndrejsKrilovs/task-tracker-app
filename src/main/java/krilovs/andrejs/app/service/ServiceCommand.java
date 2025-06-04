package krilovs.andrejs.app.service;

public interface ServiceCommand<I, O> {
  O execute(I input);
}