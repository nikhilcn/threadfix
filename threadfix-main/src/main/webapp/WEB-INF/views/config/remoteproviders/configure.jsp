<script type="text/ng-template" id="configureRemoteProviderModal.html">

    <div class="modal-header">
        <h4 id="myModalLabel">Configure {{ object.name }}</h4>
    </div>
    <div class="modal-body" ng-form="form">
        <table class="dataTable">
            <tbody>
                <tr ng-show="object.hasUserNamePassword">
                    <td class="no-color">Username</td>
                    <td class="no-color inputValue">
                        <input focus-on="focusInput" ng-model="object.username" id="usernameInput" type="text" name="username" size="50" maxlength="60" ng-required="object.hasUserNamePassword" style="width:280px"/>
                    </td>
                    <td>
                        <span class="errors" ng-show="object.hasUserNamePassword && form.username.$dirty && form.username.$error.required">Username is required.</span>
                        <span class="errors" ng-show="form.username.$dirty && form.username.$error.maxlength">Over 60 characters limit!</span>
                    </td>
                </tr>
                <tr ng-show="object.hasUserNamePassword">
                    <td class="no-color">Password</td>
                    <td class="no-color inputValue">
                        <input ng-model="object.password" id="passwordInput" type="password" name="password" size="50" maxlength="60" ng-required="object.hasUserNamePassword" style="width:280px"/>
                    </td>
                    <td>
                        <span class="errors" ng-show="object.hasUserNamePassword && form.password.$dirty && form.password.$error.required">Password is required.</span>
                        <span class="errors" ng-show="form.password.$dirty && form.password.$error.maxlength">Over 60 characters limit!</span>
                    </td>
                </tr>
                <tr ng-show="object.hasApiKey">
                    <td class="no-color">API Key</td>
                    <td class="no-color inputValue">
                        <input focus-on="focusInput" ng-model="object.apiKey" id="apiKeyInput" type="text" name="apiKey" ng-required="object.hasApiKey" size="50" maxlength="60" style="width:280px"/>
                    </td>
                    <td>
                        <span class="errors" ng-show="object.hasApiKey && form.apiKey.$dirty && form.apiKey.$error.required">API Key is required.</span>
                        <span class="errors" ng-show="form.apiKey.$dirty && form.apiKey.$error.maxlength">Over 60 characters limit!</span>
                    </td>
                </tr>
                <tr ng-show="object.isFortifyRealtime">
                    <td class="no-color">URL</td>
                    <td class="no-color inputValue">
                    <input focus-on="focusInput" ng-model="object.url" id="fortifyUrlInput" type="url" name="url" size="50" maxlength="225" style="width:280px"/>
                    </td>
                    <td>
                        <span class="errors" ng-show="object.isFortifyRealtime && form.url.$dirty && form.url.$error.required">URL is required.</span>
                    </td>
                </tr>
                <tr ng-show="object.isQualys">
                    <td class="no-color">Region</td>
                    <td class="no-color inputValue">
                        <input ng-model="object.isEuropean" type="radio" name="isEuropean" ng-value="false"> US </input>
                        <input ng-model="object.isEuropean" type="radio" name="isEuropean" ng-value="true"> EU </input>
                    </td>
                </tr>
                <tr ng-if="object.isWhiteHat">
                    <td class="no-color">Result Style</td>
                    <td class="no-color inputValue">

                        <ul id="matchSourceNumbers" class="nav nav-pills">
                            <li id="threadFixNumbers" ng-class="{ active: object.matchSourceNumbers === false }">
                                <a popover-trigger="mouseenter"
                                        ng-click="setMatchSourceNumbers(false)"
                                        popover-placement="bottom" popover="With this option selected, ThreadFix will parse the WhiteHat attack vectors as separate vulnerabilities. More data from WhiteHat improves ThreadFix's ability to present data, apply hybrid analysis, and merge WhiteHat findings with data from other sources.">
                                    ThreadFix
                                </a>
                            </li>
                            <li id="whiteHatNumbers" ng-class="{ active: object.matchSourceNumbers === true }">
                                <a popover-trigger="mouseenter"
                                    ng-click="setMatchSourceNumbers(true)"
                                    popover-placement="bottom"
                                    popover="With this option selected, ThreadFix will attempt to match the numbers that WhiteHat displays in its web UI. Selecting this option can hinder ThreadFix's ability to present data, apply hybrid analysis, and merge WhiteHat findings with data from other sources.">
                                    WhiteHat
                                </a>
                            </li>
                        </ul>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
    <%@ include file="/WEB-INF/views/modal/footer.jspf" %>

</script>