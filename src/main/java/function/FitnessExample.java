package function;

import fitnesse.responders.run.SuiteResponder;
import fitnesse.wiki.*;

public class FitnessExample {
    public String testableHtml(PageData pageData, boolean includeSuiteSetup) throws Exception {
        return new TestableHtml(pageData, includeSuiteSetup).html();
    }

    /**
     * 테스트 가능한 HTML
     */
    private class TestableHtml {
        private PageData pageData;
        private boolean includeSuiteSetup;
        private StringBuffer content;
        private WikiPage wikiPage;
        private static final String NOT_INCLUDE = "!include";

        public TestableHtml(PageData pageData, boolean includeSuiteSetup) {
            this.pageData = pageData;
            this.includeSuiteSetup = includeSuiteSetup;
            content = new StringBuffer();
            wikiPage = pageData.getWikiPage();
        }

        public String html() throws Exception {

            String command = NOT_INCLUDE + " -setup .";
            WikiPage suiteSetup = PageCrawlerImpl.getInheritedPage(SuiteResponder.SUITE_SETUP_NAME, wikiPage);
            WikiPage setup = PageCrawlerImpl.getInheritedPage("SetUp", wikiPage);
            if (isTest()) {
                addSetup(command, suiteSetup, setup);
            }

            content.append(pageData.getContent());

            String _command = NOT_INCLUDE + " -teardown .";
            WikiPage teardown = PageCrawlerImpl.getInheritedPage("TearDown", wikiPage);
            WikiPage suiteTeardown = PageCrawlerImpl.getInheritedPage(SuiteResponder.SUITE_TEARDOWN_NAME, wikiPage);
            if (isTest()) {
                addTearDown(_command, teardown, suiteTeardown);
            }

            pageData.setContent(content.toString());
            return pageData.getHtml();
        }

        private boolean isTest() throws Exception {
            return pageData.hasAttribute("Test");
        }

        private void addTearDown(String _command, WikiPage teardown, WikiPage suiteTeardown) throws Exception {
            if (teardown != null) {
                addPath(_command, teardown);
            }
            if (includeSuiteSetup) {
                if (suiteTeardown != null) {
                    addPath(_command, suiteTeardown);
                }
            }
        }

        private void addSetup(String command, WikiPage suiteSetup, WikiPage setup) throws Exception {
            if (includeSuiteSetup) {
                if (suiteSetup != null) {
                    addPath(command, suiteSetup);
                }
            }
            if (setup != null) {
                addPath(command, setup);
            }
        }

        private void addPath(String command, WikiPage testProcess) throws Exception {
            WikiPagePath path = wikiPage.getPageCrawler().getFullPath(testProcess);
            String pathName = PathParser.render(path);
            content.append(command).append(pathName).append("\n");
        }
    }
}
