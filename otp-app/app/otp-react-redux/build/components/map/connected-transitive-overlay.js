"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.default = void 0;

var _coreUtils = _interopRequireDefault(require("@opentripplanner/core-utils"));

var _transitiveOverlay = _interopRequireDefault(require("@opentripplanner/transitive-overlay"));

var _reactRedux = require("react-redux");

var _state = require("../../util/state");

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

// connect to the redux store
var mapStateToProps = function mapStateToProps(state, ownProps) {
  var activeSearch = (0, _state.getActiveSearch)(state.otp);
  var transitiveData = null;

  if (activeSearch && activeSearch.query.routingType === 'ITINERARY' && activeSearch.response && activeSearch.response.length > 0) {
    // FIXME: This may need some simplification.
    var itins = (0, _state.getActiveItineraries)(state.otp);
    var visibleIndex = activeSearch.visibleItinerary !== undefined && activeSearch.visibleItinerary !== null ? activeSearch.visibleItinerary : activeSearch.activeItinerary; // TODO: prevent itineraryToTransitive() from being called more than needed

    var visibleItinerary = itins[visibleIndex] ? itins[visibleIndex] : (0, _state.getActiveItinerary)(state.otp);
    if (visibleItinerary) transitiveData = _coreUtils.default.map.itineraryToTransitive(visibleItinerary);
  } else if (activeSearch && activeSearch.response && activeSearch.response.otp) {
    transitiveData = activeSearch.response.otp;
  }

  return {
    activeItinerary: activeSearch && activeSearch.activeItinerary,
    routingType: activeSearch && activeSearch.query && activeSearch.query.routingType,
    transitiveData: transitiveData,
    visible: true
  };
};

var mapDispatchToProps = {};

var _default = (0, _reactRedux.connect)(mapStateToProps, mapDispatchToProps)(_transitiveOverlay.default);

exports.default = _default;
module.exports = exports.default;

//# sourceMappingURL=connected-transitive-overlay.js