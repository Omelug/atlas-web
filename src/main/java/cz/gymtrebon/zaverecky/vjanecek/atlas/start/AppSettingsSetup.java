package cz.gymtrebon.zaverecky.vjanecek.atlas.start;


/*
@Component
public class AppSettingsSetup implements ApplicationListener<ApplicationReadyEvent> {
    private final AppSettingsRepository appSettingsRepository;

    public AppSettingsSetup(AppSettingsRepository appSettingsRepository) {
        this.appSettingsRepository = appSettingsRepository;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (appSettingsRepository.count() == 0) {
            // If no settings exist, redirect to setup page
            // Return a ModelAndView to your setup page template
            String setupPageUrl = "/admin/setup";
            RedirectView redirectView = new RedirectView(setupPageUrl);
            event.getApplicationContext().publishEvent(new RedirectEvent(this, redirectView));
        } else {
            // Settings exist, continue with regular flow
        }
    }
    private class RedirectEvent extends ApplicationEvent {
        private final RedirectView redirectView;

        public RedirectEvent(Object source, RedirectView redirectView) {
            super(source);
            this.redirectView = redirectView;
        }

        public RedirectView getRedirectView() {
            return redirectView;
        }
    }
}*/
