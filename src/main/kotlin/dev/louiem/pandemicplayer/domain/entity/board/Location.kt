package dev.louiem.pandemicplayer.domain.entity.board

import dev.louiem.pandemicplayer.domain.valueobject.City
import dev.louiem.pandemicplayer.domain.valueobject.Colour
import dev.louiem.pandemicplayer.exception.GameException

class Location(val city: City) {
    private var connectedLocations = setOf<Location>()
    private var hasResearchStation = false
    private var diseaseCubes = mutableMapOf(
            Colour.Blue to 0,
            Colour.Yellow to 0,
            Colour.Black to 0,
            Colour.Red to 0,
    )

    fun getConnectedLocations(): Set<Location> {
        return this.connectedLocations
    }

    fun getQtyOfDiseaseCubes(): Map<Colour, Int> {
        return diseaseCubes
    }

    fun infect(colour: Colour, qty: Int) {
        val totalCubes = (diseaseCubes[colour]?:0) + qty
        if(totalCubes > MAXIMUM_CUBES) {
            throw GameException("Attempt to place more cubes than allowed on a location. $totalCubes on $this")
        }
        diseaseCubes[colour] = totalCubes
    }

    fun treat(colour: Colour, cubesToRemove: Int) {
        if(((diseaseCubes[colour]?:0) - cubesToRemove) < 0) {
            throw GameException("Attempt to treat a location which doesn't have any disease")
        }

        diseaseCubes[colour] = diseaseCubes[colour]!! - cubesToRemove
    }

    fun addResearchStation() {
        this.hasResearchStation = true
    }

    fun hasResearchStation(): Boolean{
        return this.hasResearchStation
    }

    companion object {
        const val MAXIMUM_CUBES = 3
        private var locationMap: Map<City, Location>? = null

        fun locationMapSingleton(): Map<City, Location>  {
            return this.locationMap?: locationMapFactory()
        }

        private fun locationMapFactory(): Map<City, Location> {
            locationMap = City.values()
                    .map { Location(it) }
                    .associateBy { it.city }

            locationMap!!.values.forEach {
                when(it.city) {
                    City.SanFrancisco -> connect(it, transform(City.Tokyo, City.Manila, City.LosAngeles, City.Chicago))
                    City.Chicago -> connect(it, transform(City.SanFrancisco, City.LosAngeles, City.MexicoCity, City.Atlanta, City.Montreal))
                    City.Montreal -> connect(it, transform(City.Chicago, City.Washington, City.NewYork))
                    City.NewYork -> connect(it, transform(City.Montreal, City.Washington, City.Madrid, City.London))
                    City.Atlanta -> connect(it, transform(City.Chicago, City.Miami, City.Washington))
                    City.Washington -> connect(it, transform(City.Montreal, City.Atlanta, City.Miami, City.NewYork))
                    City.London -> connect(it, transform(City.NewYork, City.Madrid, City.Paris, City.Essen))
                    City.Essen -> connect(it, transform(City.London, City.Paris, City.Milan, City.StPetersburg))
                    City.StPetersburg -> connect(it, transform(City.Essen, City.Istanbul, City.Moscow))
                    City.Madrid -> connect(it, transform(City.NewYork, City.SaoPaulo, City.Algiers, City.Paris, City.London))
                    City.Paris -> connect(it, transform(City.London, City.Madrid, City.Algiers, City.Milan, City.Essen))
                    City.Milan -> connect(it, transform(City.Essen, City.Paris, City.Istanbul))

                    City.LosAngeles -> connect(it, transform(City.SanFrancisco, City.Sydney, City.MexicoCity, City.Chicago))
                    City.MexicoCity -> connect(it, transform(City.Chicago, City.LosAngeles, City.Lima, City.Bogota, City.Miami))
                    City.Miami -> connect(it, transform(City.Atlanta, City.MexicoCity, City.Bogota, City.Washington))
                    City.Bogota -> connect(it, transform(City.Miami, City.MexicoCity, City.Lima, City.BuenosAires, City.SaoPaulo))
                    City.Lima -> connect(it, transform(City.MexicoCity, City.Bogota, City.Santiago))
                    City.Santiago -> connect(it, transform(City.Lima))
                    City.BuenosAires -> connect(it, transform(City.Bogota, City.SaoPaulo))
                    City.SaoPaulo -> connect(it, transform(City.Bogota, City.BuenosAires, City.Lagos, City.Madrid))
                    City.Lagos -> connect(it, transform(City.SaoPaulo, City.Kinshasa, City.Khartoum))
                    City.Kinshasa -> connect(it, transform(City.Lagos, City.Johannesburg, City.Khartoum))
                    City.Johannesburg -> connect(it, transform(City.Kinshasa, City.Khartoum))
                    City.Khartoum -> connect(it, transform(City.Lagos, City.Kinshasa, City.Johannesburg, City.Cairo))

                    City.Algiers -> connect(it, transform(City.Madrid, City.Paris, City.Istanbul, City.Cairo))
                    City.Istanbul -> connect(it, transform(City.Milan, City.StPetersburg, City.Moscow, City.Algiers, City.Cairo, City.Baghdad))
                    City.Moscow -> connect(it, transform(City.StPetersburg, City.Istanbul, City.Tehran))
                    City.Cairo -> connect(it, transform(City.Algiers, City.Istanbul, City.Baghdad, City.Riyadh, City.Khartoum))
                    City.Baghdad -> connect(it, transform(City.Istanbul, City.Cairo, City.Riyadh, City.Karachi, City.Tehran))
                    City.Tehran -> connect(it, transform(City.Moscow, City.Baghdad, City.Karachi, City.Delhi))
                    City.Riyadh -> connect(it, transform(City.Cairo, City.Baghdad, City.Karachi))
                    City.Karachi -> connect(it, transform(City.Riyadh, City.Baghdad, City.Tehran, City.Delhi, City.Mumbai))
                    City.Delhi -> connect(it, transform(City.Tehran, City.Karachi, City.Mumbai, City.Chennai, City.Kolkata))
                    City.Kolkata -> connect(it, transform(City.Delhi, City.Chennai, City.Bangkok, City.HongKong))
                    City.Mumbai -> connect(it, transform(City.Karachi, City.Delhi, City.Chennai))
                    City.Chennai -> connect(it, transform(City.Mumbai, City.Delhi, City.Kolkata, City.Bangkok, City.Jakarta))

                    City.Beijing -> connect(it, transform(City.Shanghai, City.Seoul))
                    City.Seoul -> connect(it, transform(City.Beijing, City.Shanghai, City.Tokyo))
                    City.Tokyo -> connect(it, transform(City.Seoul, City.Shanghai, City.SanFrancisco))
                    City.Osaka -> connect(it, transform(City.Tokyo, City.Taipei))
                    City.Taipei -> connect(it, transform(City.Osaka, City.Shanghai, City.HongKong, City.Manila))
                    City.HongKong -> connect(it, transform(City.Shanghai, City.Taipei, City.Manila, City.HoChiMinhCity, City.Bangkok, City.Kolkata))
                    City.Shanghai -> connect(it, transform(City.Beijing, City.Seoul, City.Tokyo, City.Taipei, City.HongKong))
                    City.Bangkok -> connect(it, transform(City.Kolkata, City.Chennai, City.Jakarta, City.HoChiMinhCity, City.HongKong))
                    City.Jakarta -> connect(it, transform(City.Chennai, City.Bangkok, City.HoChiMinhCity, City.Sydney))
                    City.HoChiMinhCity -> connect(it, transform(City.Jakarta, City.Bangkok, City.HongKong, City.Manila))
                    City.Manila -> connect(it, transform(City.HoChiMinhCity, City.HongKong, City.Taipei, City.SanFrancisco, City.Sydney))
                    City.Sydney -> connect(it, transform(City.Jakarta, City.Manila, City.LosAngeles))
                }
            }
            locationMap!![City.Atlanta]!!.addResearchStation()
            return locationMap!!
        }

        private fun connect(location: Location, locations: Set<Location>) {
            location.connectedLocations = locations
        }

        private fun transform(vararg cities: City): Set<Location> {
            return cities.map { locationMap?.get(it)!! }.toSet()
        }
    }
}