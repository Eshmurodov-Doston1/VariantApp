package uz.gxteam.variantapp.adapters.mainViewAdapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import uz.gxteam.variantapp.ui.main.mainView.MainViewFragment

class MainViewPagerAdapter(fragmentActivity: FragmentActivity):FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
      return 2
    }

    override fun createFragment(position: Int): Fragment {
        return MainViewFragment.newInstance(position)
    }
}