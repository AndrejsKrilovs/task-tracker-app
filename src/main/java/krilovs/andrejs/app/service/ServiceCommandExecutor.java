package krilovs.andrejs.app.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

@ApplicationScoped
public class ServiceCommandExecutor {

  @Inject
  Instance<ServiceCommand<?, ?>> commands;

  public <I, O> O run(Class<? extends ServiceCommand<I, O>> commandClass, I input) {
    ServiceCommand<I, O> command = commands.select(commandClass).get();
    return command.execute(input);
  }
}
