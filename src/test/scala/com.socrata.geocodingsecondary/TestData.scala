package com.socrata.geocodingsecondary

import com.rojoma.json.v3.ast.{JNumber, JNull, JString, JObject}
import com.socrata.datacoordinator.id.{StrategyType, ColumnId, UserColumnId}
import com.socrata.datacoordinator.secondary.ComputationStrategyInfo
import com.socrata.datacoordinator.secondary.feedback.{CopyNumber, DataVersion, CookieSchema}
import com.socrata.datacoordinator.util.collection.ColumnIdMap
import com.socrata.geocoders.{InternationalAddress, LatLon}
import com.socrata.soql.types.{SoQLNumber, SoQLID, SoQLNull, SoQLText}

object TestData {

  val geocodingStrategyType = StrategyType("geocoding")

  def strategyInfo(sourceColumnIds: Seq[UserColumnId], parameters: JObject) =
    ComputationStrategyInfo(geocodingStrategyType, sourceColumnIds, parameters)

  case class Column(id: ColumnId, userId: UserColumnId)

  def column(id: Long, userId: String) = Column(new ColumnId(id), new UserColumnId(userId))

  // id column
  val id = column(1, ":id")

  // source columns
  val address    = column(2, "addr-esss")
  val locality   = column(3, "loca-lity")
  val subregion  = column(4, "subr-egio")
  val region     = column(5, "regi-onnn")
  val postalCode = column(6, "post-alco")
  val country    = column(7, "coun-tryy")

  val sourceColumns = Seq(address, locality, subregion, region, postalCode, country)

  // target column
  val point = column(8, "poin-tttt")
  val targetColId = point.userId

  val columns = Seq(id, address, locality, subregion, region, postalCode, country, point)
  val columnIdMap = columns.map(col => (col.userId, col.id.underlying)).toMap

  // source column ids
  val emptySourceColumnIds = Seq.empty
  val sourceColumnIds = sourceColumns.map(_.userId)

  // parameters
  val emptyParameters = JObject(Map(
    "country_default" -> JString("US")
  ))

  val parameters = JObject(Map(
    "address" -> JString(address.userId.underlying),
    "locality" -> JString(locality.userId.underlying),
    "region" -> JString(region.userId.underlying),
    "postal_code" -> JString(postalCode.userId.underlying),
    "country" -> JString(country.userId.underlying),
    "country_default" -> JString("US")
  ))

  val parametersNoPostalCode = JObject(Map(
    "address" -> JString(address.userId.underlying),
    "locality" -> JString(locality.userId.underlying),
    "region" -> JString(region.userId.underlying),
    "country" -> JString(country.userId.underlying),
    "country_default" -> JString("US")
  ))

  val parametersNoCountry = JObject(Map(
    "address" -> JString(address.userId.underlying),
    "locality" -> JString(locality.userId.underlying),
    "region" -> JString(region.userId.underlying),
    "postal_code" -> JString(postalCode.userId.underlying),
    "country_default" -> JString("US")
  ))

  val parametersDefaultRegion = JObject(Map(
    "address" -> JString(address.userId.underlying),
    "locality" -> JString(locality.userId.underlying),
    "region" -> JString(region.userId.underlying),
    "postal_code" -> JString(postalCode.userId.underlying),
    "country" -> JString(country.userId.underlying),
    "region_default" -> JString("WA"),
    "country_default" -> JString("US")
  ))

  val parametersOnlyDefaultRegion = JObject(Map(
    "address" -> JString(address.userId.underlying),
    "locality" -> JString(locality.userId.underlying),
    "postal_code" -> JString(postalCode.userId.underlying),
    "country" -> JString(country.userId.underlying),
    "region_default" -> JString("WA"),
    "country_default" -> JString("US")
  ))

  val parametersDefaultCountry = JObject(Map(
    "address" -> JString(address.userId.underlying),
    "locality" -> JString(locality.userId.underlying),
    "region" -> JString(region.userId.underlying),
    "postal_code" -> JString(postalCode.userId.underlying),
    "country" -> JString(country.userId.underlying),
    "country_default" -> JString("United States")
  ))

  val parametersOnlyDefaultCountry = JObject(Map(
    "address" -> JString(address.userId.underlying),
    "locality" -> JString(locality.userId.underlying),
    "region" -> JString(region.userId.underlying),
    "postal_code" -> JString(postalCode.userId.underlying),
    "country_default" -> JString("United States")
  ))

  val parametersWithExtra = JObject(Map(
    "address" -> JString(address.userId.underlying),
    "locality" -> JString(locality.userId.underlying),
    "region" -> JString(region.userId.underlying),
    "postal_code" -> JString(postalCode.userId.underlying),
    "country" -> JString(country.userId.underlying),
    "country_default" -> JString("US"),
    "extra" -> JNull
  ))

  val parametersMalformed = JObject(Map(
    "address" -> JString(address.userId.underlying),
    "locality" -> JString(locality.userId.underlying),
    "region" -> JNumber(666),
    "postal_code" -> JString(postalCode.userId.underlying),
    "country" -> JString(country.userId.underlying),
    "country_default" -> JString("US"),
    "extra" -> JNull
  ))

  def cookieSchema(strategyInfo: ComputationStrategyInfo) = CookieSchema(
    dataVersion = DataVersion(44),
    copyNumber = CopyNumber(4),
    primaryKey = id.userId,
    columnIdMap,
    strategyMap = Map(targetColId -> strategyInfo),
    obfuscationKey = "obfuscate".getBytes,
    computationRetriesLeft = 6,
    mutationScriptRetriesLeft = 6,
    resync = false,
    JNull
  )

  def textOrNull(opt: Option[String]) = opt.map(SoQLText(_)).getOrElse(SoQLNull)

  def row(addrOpt: Option[InternationalAddress], nullCountry: Boolean = false, postalCodeAsNumber: Boolean = false) =
    addrOpt match {
      case Some(addr) => ColumnIdMap(
        id.id -> SoQLID(5),
        address.id -> textOrNull(addr.address),
        locality.id -> textOrNull(addr.locality),
        subregion.id -> textOrNull(addr.subregion),
        region.id -> textOrNull(addr.region),
        postalCode.id -> { addr.postalCode match {
          case Some(str) => if (postalCodeAsNumber) SoQLNumber(new java.math.BigDecimal(str)) else SoQLText(str)
          case None => SoQLNull
        }},
        country.id -> { if (nullCountry) SoQLNull else SoQLText(addr.country) },
        point.id -> SoQLNull)
      case None =>  ColumnIdMap(
        id.id -> SoQLID(5),
        address.id -> SoQLNull,
        locality.id -> SoQLNull,
        subregion.id -> SoQLNull,
        region.id -> SoQLNull,
        postalCode.id -> SoQLNull,
        country.id -> SoQLNull,
        point.id -> SoQLNull)
    }

  // rows and addresses
  val baseRow = ColumnIdMap(id.id -> SoQLID(5), point.id -> SoQLNull)

  val emptyAddress = InternationalAddress(None, None, None, None, None, None) // actually None
  val emptyRow = row(emptyAddress, nullCountry = true)
  val emptyJValue = JNull
  val usJValue = JString("POINT(36.2474412 -113.7152476)")

  val socrataAddress = InternationalAddress(Some("705 5th Ave S #600"), Some("Seattle"), None, Some("WA"), Some("98104"), Some("US"))
  val socrataRow = row(socrataAddress)
  val socrataJValue = JString("POINT(47.5964756 -122.3303628)")

  val socrataAddressNoRegion = socrataAddress.map(_.copy(region = None))
  val socrataRowNoRegion = row(socrataAddressNoRegion)

  val socrataAddressNoPostalCode = socrataAddress.map(_.copy(postalCode = None))

  val socrataRowPostalCodeAsNumber = row(socrataAddress, nullCountry = false, postalCodeAsNumber = true)

  val socrataRowNullCountry = row(socrataAddress, nullCountry = true)

  val socrataAddressUnitedStates = socrataAddress.map(_.copy(country = "United States"))
  val socrataRowUnitedStates = row(socrataAddressUnitedStates)

  val socrataDCAddress = InternationalAddress(Some("1150 17th St NW #200"), Some("Washington"), None, Some("DC"), Some("20036"), Some("US"))
  val socrataDCRow = row(socrataDCAddress)
  val socrataDCJValue = JString("POINT(38.9053532 -77.0410809)")

  val nowhereAddress = InternationalAddress(Some("101 Nowhere Lane"), None, None, Some("Nowhere Land"), None, Some("USA"))
  val nowhereRow = row(nowhereAddress)
  val nowhereJValue = JNull

  val badAddress = InternationalAddress(Some("Bad Address Lane"), None, None, None, None, None)
  val badAddressRow = row(badAddress)

  val knownAddresses = Map(
    socrataAddress -> LatLon(47.5964756, -122.3303628),
    socrataDCAddress -> LatLon(38.9053532, -77.0410809)
  )

}
