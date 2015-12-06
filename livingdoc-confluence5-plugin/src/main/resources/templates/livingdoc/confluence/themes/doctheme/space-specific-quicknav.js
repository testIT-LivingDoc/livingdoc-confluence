AJS.toInit(function($) {
    // This code runs on every page, but we only want it to run in the Doc theme.
    if ($("#com-atlassian-confluence").hasClass("theme-documentation")) {
        var quickSearchQuery = $("#quick-search-query");

        var siteSearchForm = quickSearchQuery.closest("form");
        var spaceKey = $("fieldset input[name='spaceSearch']", siteSearchForm).val() === "true" ? $("#confluence-space-key").attr("content") : "";

        siteSearchForm.submit(function() {
            var query = quickSearchQuery.val();

            if (query.search(/all:/gi) >= 0) {
                quickSearchQuery.val($.trim(query.replace(/all:/gi, '')));
            }
        });

        var quickNav = AJS.defaultIfUndefined("Atlassian.SearchableApps.QuickNav", { defaultValue: Confluence.QuickNav });

        AJS.log("Applying doc-theme quick search");
        quickNav.addDropDownPostProcess(function (dropDown) {
            if (spaceKey) {
                var searchFor = $("a.search-for", dropDown);
                searchFor.attr("href", searchFor.attr("href") + "&where=" + encodeURIComponent(spaceKey));
            }
        });
        quickNav.setMakeParams(function(value) {
            var params = { query : value };

            if (params.query.search(/all:/gi) >= 0) {
                $("input[name='where']", siteSearchForm).val("");
                params.query = $.trim(params.query.replace(/all:/gi, ''));
                if (!params.query)
                    params.query = "ALL";
            } else if (spaceKey) {
                params.spaceKey = spaceKey;
            }

            return params;
        });

        // DOC-79 - We need to overwrite the tooltip that Confluence appended to the quick search box when each page finish loading.
        quickSearchQuery.mouseover(function() {
            if(spaceKey) {
                quickSearchQuery.attr("title", $("input[name='tooltip']", siteSearchForm).val());
            }
        });
    }
});