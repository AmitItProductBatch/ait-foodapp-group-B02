import React, { useState, useEffect } from 'react'
import {
  Utensils,
  ShoppingBag,
  Clock,
  MapPin,
  Plus,
  Minus,
  Trash2,
  CheckCircle2,
  AlertCircle,
  Star,
  User,
  Users,
  Search,
  Filter,
  CreditCard,
  Building,
  RefreshCw,
  X,
  ChevronRight,
  ShieldCheck,
  Truck,
  ArrowRight,
  Sliders,
  DollarSign
} from 'lucide-react'
import * as api from './api'

export default function App() {
  // Navigation
  const [activeTab, setActiveTab] = useState('menu') // 'menu' | 'orders' | 'reviews' | 'admin'
  const [cartOpen, setCartOpen] = useState(false)
  const [serverOnline, setServerOnline] = useState(null)
  const [loading, setLoading] = useState(false)
  const [toasts, setToasts] = useState([])

  // Core App State
  const [users, setUsers] = useState([])
  const [currentUser, setCurrentUser] = useState(null)
  const [restaurants, setRestaurants] = useState([])
  const [currentRestaurant, setCurrentRestaurant] = useState(null)
  const [foodItems, setFoodItems] = useState([])
  const [cart, setCart] = useState({ cartId: 0, items: [], totalAmount: 0 })
  const [orders, setOrders] = useState([])
  const [payments, setPayments] = useState([])
  const [feedbacks, setFeedbacks] = useState([])
  const [pricingRules, setPricingRules] = useState([])
  
  // Delivery Fee & Distance Calculation
  const [selectedAddressId, setSelectedAddressId] = useState(null)
  const [deliveryInfo, setDeliveryInfo] = useState({ distance: 0, deliveryFee: 30, loading: false })
  const [paymentMethod, setPaymentMethod] = useState('CARD')

  // Filters & Search
  const [searchQuery, setSearchQuery] = useState('')
  const [typeFilter, setTypeFilter] = useState('ALL') // 'ALL' | 'VEG' | 'NON_VEG'
  const [selectedCuisine, setSelectedCuisine] = useState('ALL')

  // Modals & Stripe Mock State
  const [showUserModal, setShowUserModal] = useState(false)
  const [showAddressModal, setShowAddressModal] = useState(false)
  const [showReviewModal, setShowReviewModal] = useState(false)
  const [showPaymentModal, setShowPaymentModal] = useState(false)
  const [stripePaymentData, setStripePaymentData] = useState(null)
  const [selectedOrderForAction, setSelectedOrderForAction] = useState(null)

  // Forms State
  const [userForm, setUserForm] = useState({
    name: '',
    email: '',
    mobile: '',
    role: 'CUSTOMER',
    password: 'Password@123',
    // Initial Address
    houseNo: 'Flat 101',
    buildingName: 'Galaxy Apts',
    street: 'Main Road',
    landmark: 'Near City Mall',
    area: 'Central',
    city: 'Pune',
    state: 'Maharashtra',
    pincode: 411001,
    addressType: 'HOME',
    latitude: 18.5204,
    longitude: 73.8567
  })

  const [addressForm, setAddressForm] = useState({
    houseNo: '',
    buildingName: '',
    street: '',
    landmark: '',
    area: '',
    city: 'Pune',
    state: 'Maharashtra',
    pincode: 411038,
    addressType: 'HOME',
    latitude: 18.5074,
    longitude: 73.8077
  })

  const [reviewForm, setReviewForm] = useState({
    rating: 5,
    comment: '',
    foodItemId: null
  })

  const [adminRestForm, setAdminRestForm] = useState({
    name: '',
    phone: '',
    email: '',
    description: '',
    open: true
  })

  const [adminFoodForm, setAdminFoodForm] = useState({
    foodname: '',
    foodtype: 'VEG',
    description: '',
    cuisine: 'North Indian',
    price: 250,
    available: true
  })

  const [adminPricingForm, setAdminPricingForm] = useState({
    basefees: 30,
    perKmRate: 10,
    maxdelieveryradius: 15,
    freeDelievery: 500,
    active: true
  })

  // Toast Helper
  const showToast = (message, type = 'success') => {
    const id = Date.now()
    setToasts((prev) => [...prev, { id, message, type }])
    setTimeout(() => {
      setToasts((prev) => prev.filter((t) => t.id !== id))
    }, 4000)
  }

  // ----------------------------------------------------------------------------
  // Data Fetching
  // ----------------------------------------------------------------------------
  const loadInitialData = async () => {
    setLoading(true)
    try {
      // 1. Health check
      try {
        const health = await api.getHealth()
        setServerOnline(health.status === 'UP')
      } catch {
        setServerOnline(false)
      }

      // 2. Fetch Users
      const usersData = await api.getAllUsers().catch(() => [])
      setUsers(Array.isArray(usersData) ? usersData : [])
      if (usersData && usersData.length > 0 && !currentUser) {
        setCurrentUser(usersData[0])
        if (usersData[0].addresses && usersData[0].addresses.length > 0) {
          setSelectedAddressId(usersData[0].addresses[0].addressId || usersData[0].addresses[0].id)
        }
      }

      // 3. Fetch Restaurants
      const restData = await api.getAllRestaurants().catch(() => [])
      setRestaurants(Array.isArray(restData) ? restData : [])
      if (restData && restData.length > 0 && !currentRestaurant) {
        setCurrentRestaurant(restData[0])
      }

      // 4. Fetch Food Items
      const foodData = await api.getAllFoodItems().catch(() => [])
      setFoodItems(Array.isArray(foodData) ? foodData : [])

      // 5. Fetch Orders
      const orderData = await api.getAllOrders().catch(() => [])
      setOrders(Array.isArray(orderData) ? orderData : [])

      // 6. Fetch Feedback
      const fbData = await api.getAllFeedback().catch(() => [])
      setFeedbacks(Array.isArray(fbData) ? fbData : [])

      // 7. Pricing Rules
      const rulesData = await api.getAllDeliveryPricingRules().catch(() => [])
      setPricingRules(Array.isArray(rulesData) ? rulesData : [])

      // 8. Payments Ledger
      const payData = await api.getAllPayments().catch(() => [])
      setPayments(Array.isArray(payData) ? payData : [])

    } catch (err) {
      console.error('Error loading initial data:', err)
      showToast(`Data load notice: ${err.message}`, 'error')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadInitialData()
  }, [])

  // Sync Cart whenever Current User changes
  const fetchUserCart = async (userId) => {
    if (!userId) return
    try {
      const cartData = await api.getCartByUserId(userId)
      if (cartData) {
        setCart({
          cartId: cartData.cartId || 0,
          restaurantName: cartData.restaurantName || '',
          restaurantId: cartData.restaurantId || null,
          items: cartData.items || [],
          totalAmount: cartData.totalAmount || 0
        })
      }
    } catch {
      // If cart not initialized yet, that's normal
      setCart({ cartId: 0, items: [], totalAmount: 0 })
    }
  }

  useEffect(() => {
    if (currentUser?.id) {
      fetchUserCart(currentUser.id)
    }
  }, [currentUser])

  // ----------------------------------------------------------------------------
  // Cart Actions
  // ----------------------------------------------------------------------------
  const handleAddToCart = async (foodItem) => {
    if (!currentUser) {
      showToast('Please select or register a customer first', 'error')
      setShowUserModal(true)
      return
    }

    try {
      let activeCartId = cart.cartId

      // If cart doesn't exist or is for a different restaurant, initialize it
      if (!activeCartId || activeCartId === 0) {
        const targetRestId = foodItem.restaurantId || currentRestaurant?.id || 1
        await api.createCart(currentUser.id, targetRestId).catch(() => {})
        // Re-fetch to get new cart ID
        const freshCart = await api.getCartByUserId(currentUser.id)
        activeCartId = freshCart.cartId
      }

      await api.addCartItem(activeCartId, foodItem.foodid, 1)
      showToast(`Added "${foodItem.foodname}" to your cart!`, 'success')
      await fetchUserCart(currentUser.id)
    } catch (err) {
      showToast(err.message, 'error')
    }
  }

  const handleRemoveFromCart = async (cartItemId) => {
    if (!cartItemId) return
    try {
      await api.deleteCartItem(cartItemId)
      showToast('Item removed from cart', 'info')
      await fetchUserCart(currentUser.id)
    } catch (err) {
      showToast(err.message, 'error')
    }
  }

  // Delivery Fee Calculation
  useEffect(() => {
    const updateDeliveryFee = async () => {
      if (!cart.items || cart.items.length === 0 || !selectedAddressId) return
      setDeliveryInfo((prev) => ({ ...prev, loading: true }))
      try {
        const restAddrId = 1 // default active restaurant address
        const res = await api.calculateDeliveryFee(restAddrId, selectedAddressId, cart.cartId)
        if (res && res.deliveryFee !== undefined) {
          setDeliveryInfo({
            distance: res.distance || 2.5,
            deliveryFee: res.deliveryFee,
            loading: false
          })
        }
      } catch {
        // Fallback default calculation
        const freeThreshold = 500
        const isFree = cart.totalAmount >= freeThreshold
        setDeliveryInfo({
          distance: 3.2,
          deliveryFee: isFree ? 0 : 30,
          loading: false
        })
      }
    }
    updateDeliveryFee()
  }, [cart.items, selectedAddressId, cart.totalAmount])

  // ----------------------------------------------------------------------------
  // Checkout & Order Placement
  // ----------------------------------------------------------------------------
  const handlePlaceOrder = async () => {
    if (!currentUser || !currentUser.id) {
      showToast('Please select a customer', 'error')
      return
    }
    if (!cart.items || cart.items.length === 0) {
      showToast('Your cart is empty', 'error')
      return
    }
    if (!selectedAddressId) {
      showToast('Please select a delivery address', 'error')
      return
    }

    setLoading(true)
    try {
      const orderPayload = {
        userId: currentUser.id,
        deliveryAddressId: selectedAddressId,
        paymentMethod: paymentMethod === 'CARD' ? 'STRIPE' : paymentMethod
      }

      const createdOrder = await api.createOrder(orderPayload)

      if (paymentMethod === 'CARD') {
        // Initiate Stripe Mock Gateway Payment
        try {
          const initRes = await api.initiatePayment({
            orderId: createdOrder.orderId,
            userId: currentUser.id,
            amount: createdOrder.totalAmount,
            paymentMethod: 'STRIPE'
          })

          setStripePaymentData({
            ...initRes,
            order: createdOrder
          })
          setShowPaymentModal(true)
          showToast(`Order #${createdOrder.orderId} created! Complete mock payment below.`, 'info')
        } catch (stripeErr) {
          showToast(`Stripe Gateway notice: ${stripeErr.message}`, 'error')
        }
      } else {
        // Direct record for UPI / COD
        const payPayload = {
          transactionId: `TXN_${Date.now().toString().slice(-6)}`,
          orderId: createdOrder.orderId,
          userId: currentUser.id,
          amount: createdOrder.totalAmount,
          paymentMethod: paymentMethod,
          paymentStatus: paymentMethod === 'COD' ? 'PENDING' : 'SUCCESS'
        }
        await api.createPayment(payPayload).catch(() => {})
        showToast(`🎉 Order #${createdOrder.orderId || ''} Placed Successfully!`, 'success')
      }

      setCart({ cartId: 0, items: [], totalAmount: 0 })
      setCartOpen(false)
      
      if (paymentMethod !== 'CARD') {
        setActiveTab('orders')
      }
      
      // Refresh Orders & Payments
      const orderData = await api.getAllOrders().catch(() => [])
      setOrders(Array.isArray(orderData) ? orderData : [])
      const payData = await api.getAllPayments().catch(() => [])
      setPayments(Array.isArray(payData) ? payData : [])
    } catch (err) {
      showToast(`Order failed: ${err.message}`, 'error')
    } finally {
      setLoading(false)
    }
  }

  // Authorize & Complete Stripe Mock Payment
  const handleAuthorizeStripePayment = async () => {
    if (!stripePaymentData || !stripePaymentData.paymentId) return
    setLoading(true)
    try {
      await api.updatePaymentStatus(stripePaymentData.paymentId, 'PAID')
      showToast('💳 Stripe Mock Payment Authorized & Confirmed!', 'success')
      setShowPaymentModal(false)
      setStripePaymentData(null)
      setActiveTab('orders')
      loadInitialData()
    } catch (err) {
      showToast(`Payment authorization error: ${err.message}`, 'error')
    } finally {
      setLoading(false)
    }
  }

  // ----------------------------------------------------------------------------
  // User Registration
  // ----------------------------------------------------------------------------
  const handleRegisterUser = async (e) => {
    e.preventDefault()
    setLoading(true)
    try {
      // 1. Create User
      const userRes = await api.createUser({
        name: userForm.name.trim(),
        email: userForm.email.trim(),
        mobile: userForm.mobile.trim(),
        role: userForm.role,
        password: userForm.password
      })

      // Refresh Users list
      const freshUsers = await api.getAllUsers()
      setUsers(freshUsers)
      const newlyCreated = freshUsers.find((u) => u.email === userForm.email.trim()) || freshUsers[freshUsers.length - 1]
      
      if (newlyCreated && newlyCreated.id) {
        // 2. Save Address for the new user
        await api.saveUserAddress({
          userId: newlyCreated.id,
          houseNo: userForm.houseNo,
          buildingName: userForm.buildingName,
          street: userForm.street,
          landmark: userForm.landmark,
          area: userForm.area,
          city: userForm.city,
          state: userForm.state,
          pincode: Number(userForm.pincode),
          addressType: userForm.addressType,
          latitude: Number(userForm.latitude),
          longitude: Number(userForm.longitude)
        })

        const refreshedUser = await api.getUserById(newlyCreated.id).catch(() => newlyCreated)
        setCurrentUser(refreshedUser)
        if (refreshedUser.addresses && refreshedUser.addresses.length > 0) {
          setSelectedAddressId(refreshedUser.addresses[0].addressId || refreshedUser.addresses[0].id)
        }
      }

      showToast(`Customer "${userForm.name}" created and logged in!`, 'success')
      setShowUserModal(false)
    } catch (err) {
      showToast(err.message, 'error')
    } finally {
      setLoading(false)
    }
  }

  // ----------------------------------------------------------------------------
  // Add Address Modal Submit
  // ----------------------------------------------------------------------------
  const handleAddAddress = async (e) => {
    e.preventDefault()
    if (!currentUser?.id) return
    setLoading(true)
    try {
      await api.saveUserAddress({
        userId: currentUser.id,
        ...addressForm,
        pincode: Number(addressForm.pincode),
        latitude: Number(addressForm.latitude),
        longitude: Number(addressForm.longitude)
      })
      const refreshedUser = await api.getUserById(currentUser.id)
      setCurrentUser(refreshedUser)
      if (refreshedUser.addresses && refreshedUser.addresses.length > 0) {
        const latest = refreshedUser.addresses[refreshedUser.addresses.length - 1]
        setSelectedAddressId(latest.addressId || latest.id)
      }
      showToast('New delivery address saved!', 'success')
      setShowAddressModal(false)
    } catch (err) {
      showToast(err.message, 'error')
    } finally {
      setLoading(false)
    }
  }

  // ----------------------------------------------------------------------------
  // Feedback Submit
  // ----------------------------------------------------------------------------
  const handleSubmitReview = async (e) => {
    e.preventDefault()
    if (!currentUser?.id || !currentRestaurant?.id) return
    setLoading(true)
    try {
      await api.createFeedback({
        userId: currentUser.id,
        restaurantId: currentRestaurant.id,
        foodItemId: reviewForm.foodItemId || null,
        rating: Number(reviewForm.rating),
        comment: reviewForm.comment
      })
      showToast('Thank you! Review submitted successfully.', 'success')
      setShowReviewModal(false)
      const fbData = await api.getAllFeedback().catch(() => [])
      setFeedbacks(Array.isArray(fbData) ? fbData : [])
    } catch (err) {
      showToast(err.message, 'error')
    } finally {
      setLoading(false)
    }
  }

  // ----------------------------------------------------------------------------
  // Admin Handlers
  // ----------------------------------------------------------------------------
  const handleAdminAddRestaurant = async (e) => {
    e.preventDefault()
    try {
      const res = await api.createRestaurant(adminRestForm)
      showToast(`Restaurant "${res.name}" created!`, 'success')
      const allRests = await api.getAllRestaurants()
      setRestaurants(allRests)
      setAdminRestForm({ name: '', phone: '', email: '', description: '', open: true })
    } catch (err) {
      showToast(err.message, 'error')
    }
  }

  const handleAdminAddFoodItem = async (e) => {
    e.preventDefault()
    if (!currentRestaurant?.id) return
    try {
      await api.createFoodItem({
        ...adminFoodForm,
        price: Number(adminFoodForm.price),
        restaurantId: currentRestaurant.id
      })
      showToast(`Dish "${adminFoodForm.foodname}" added to menu!`, 'success')
      const allFood = await api.getAllFoodItems()
      setFoodItems(allFood)
      setAdminFoodForm({ foodname: '', foodtype: 'VEG', description: '', cuisine: 'North Indian', price: 250, available: true })
    } catch (err) {
      showToast(err.message, 'error')
    }
  }

  const handleAdminSavePricing = async (e) => {
    e.preventDefault()
    try {
      await api.saveDeliveryPricingRule({
        basefees: Number(adminPricingForm.basefees),
        perKmRate: Number(adminPricingForm.perKmRate),
        maxdelieveryradius: Number(adminPricingForm.maxdelieveryradius),
        freeDelievery: Number(adminPricingForm.freeDelievery),
        active: true
      })
      showToast('Delivery pricing rules updated!', 'success')
      const rules = await api.getAllDeliveryPricingRules().catch(() => [])
      setPricingRules(rules)
    } catch (err) {
      showToast(err.message, 'error')
    }
  }

  // ----------------------------------------------------------------------------
  // Filtered Food Items
  // ----------------------------------------------------------------------------
  const filteredFoodItems = foodItems.filter((item) => {
    const matchesSearch = item.foodname?.toLowerCase().includes(searchQuery.toLowerCase()) ||
                          item.description?.toLowerCase().includes(searchQuery.toLowerCase())
    const matchesType = typeFilter === 'ALL' || item.foodtype?.toUpperCase() === typeFilter
    const matchesCuisine = selectedCuisine === 'ALL' || item.cuisine?.toLowerCase() === selectedCuisine.toLowerCase()
    const matchesRestaurant = !currentRestaurant?.id || item.restaurantId === currentRestaurant.id
    return matchesSearch && matchesType && matchesCuisine && matchesRestaurant
  })

  // Unique cuisines for filter
  const cuisinesList = ['ALL', ...new Set(foodItems.map((f) => f.cuisine).filter(Boolean))]

  const totalCartItemCount = cart.items?.reduce((acc, item) => acc + item.quantity, 0) || 0
  const grandTotal = (cart.totalAmount || 0) + (deliveryInfo.deliveryFee || 0)

  return (
    <div className="app-layout">
      {/* Background ambient lighting */}
      <div className="ambient-glow ambient-1"></div>
      <div className="ambient-glow ambient-2"></div>

      {/* Toast Notification Stack */}
      <div className="toast-container">
        {toasts.map((t) => (
          <div key={t.id} className={`toast ${t.type}`}>
            {t.type === 'success' && <CheckCircle2 size={18} color="var(--success)" />}
            {t.type === 'error' && <AlertCircle size={18} color="var(--danger)" />}
            {t.type === 'info' && <Truck size={18} color="var(--accent)" />}
            <span>{t.message}</span>
          </div>
        ))}
      </div>

      {/* Top Navbar */}
      <header className="navbar">
        <div className="nav-container">
          <div className="brand-section" onClick={() => setActiveTab('menu')}>
            <div className="brand-logo-badge">🍔</div>
            <div>
              <div className="brand-title">FoodDelivery</div>
              <span className="brand-tag">Group B02 Platform</span>
            </div>
          </div>

          <nav className="nav-links">
            <button
              className={`nav-btn ${activeTab === 'menu' ? 'active' : ''}`}
              onClick={() => setActiveTab('menu')}
            >
              <Utensils size={16} /> Explore Menu
            </button>
            <button
              className={`nav-btn ${activeTab === 'orders' ? 'active' : ''}`}
              onClick={() => setActiveTab('orders')}
            >
              <Clock size={16} /> My Orders
              {orders.length > 0 && <span className="cart-count-badge" style={{ background: '#3b82f6', color: '#fff' }}>{orders.length}</span>}
            </button>
            <button
              className={`nav-btn ${activeTab === 'reviews' ? 'active' : ''}`}
              onClick={() => setActiveTab('reviews')}
            >
              <Star size={16} /> Community Reviews
            </button>
            <button
              className={`nav-btn ${activeTab === 'admin' ? 'active' : ''}`}
              onClick={() => setActiveTab('admin')}
            >
              <Sliders size={16} /> Admin Hub
            </button>
          </nav>

          <div className="nav-right">
            {/* Active Customer Selector */}
            <div className="selector-box" title="Switch Customer">
              <User size={16} color="var(--primary)" />
              <select
                value={currentUser?.id || ''}
                onChange={(e) => {
                  const u = users.find((x) => x.id === Number(e.target.value))
                  if (u) setCurrentUser(u)
                }}
              >
                {users.map((u) => (
                  <option key={u.id} value={u.id}>
                    {u.name} (#{u.id})
                  </option>
                ))}
              </select>
              <button
                className="btn-icon"
                title="Register New Customer"
                onClick={() => setShowUserModal(true)}
                style={{ background: 'transparent', border: 'none', color: 'var(--primary)', cursor: 'pointer' }}
              >
                <Plus size={16} />
              </button>
            </div>

            {/* Cart Button */}
            <button className="btn-cart" onClick={() => setCartOpen(true)}>
              <ShoppingBag size={18} />
              <span>Cart</span>
              {totalCartItemCount > 0 && <span className="cart-count-badge">{totalCartItemCount}</span>}
            </button>
          </div>
        </div>
      </header>

      {/* Main Content Body */}
      <main className="main-content">
        {/* TAB 1: MENU & RESTAURANT BROWSING */}
        {activeTab === 'menu' && (
          <div>
            {/* Restaurant Hero Banner */}
            <div className="restaurant-hero">
              <div className="hero-info">
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '0.5rem' }}>
                  <div className="selector-box" style={{ background: 'rgba(255,255,255,0.06)' }}>
                    <Building size={16} color="var(--primary)" />
                    <select
                      value={currentRestaurant?.id || ''}
                      onChange={(e) => {
                        const r = restaurants.find((x) => x.id === Number(e.target.value))
                        if (r) setCurrentRestaurant(r)
                      }}
                    >
                      {restaurants.map((r) => (
                        <option key={r.id} value={r.id}>
                          {r.name}
                        </option>
                      ))}
                    </select>
                  </div>
                  <span className={`status-badge ${currentRestaurant?.open !== false ? 'open' : 'closed'}`}>
                    {currentRestaurant?.open !== false ? '● Open Now' : '● Closed'}
                  </span>
                </div>

                <h1>{currentRestaurant?.name || 'Gourmet Kitchen'}</h1>
                <p className="hero-desc">{currentRestaurant?.description || 'Authentic delicacies delivered hot and fresh to your doorstep.'}</p>

                <div className="hero-meta-row">
                  <div className="meta-item">
                    <Star size={16} color="#fbbf24" fill="#fbbf24" />
                    <strong>4.8</strong> (240+ reviews)
                  </div>
                  <div className="meta-item">
                    <Clock size={16} />
                    <span>25-35 mins delivery</span>
                  </div>
                  <div className="meta-item">
                    <Truck size={16} />
                    <span>Free delivery on orders above ₹500</span>
                  </div>
                </div>
              </div>

              <div>
                <button
                  className="btn-secondary"
                  onClick={() => setShowReviewModal(true)}
                  style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}
                >
                  <Star size={16} color="#fbbf24" /> Write a Review
                </button>
              </div>
            </div>

            {/* Filter & Search Controls */}
            <div className="filter-bar">
              <div className="search-box">
                <Search size={18} className="search-icon" />
                <input
                  type="text"
                  placeholder="Search dishes, biryani, burgers, pasta..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                />
              </div>

              <div className="filter-pills">
                <button
                  className={`filter-pill ${typeFilter === 'ALL' ? 'active' : ''}`}
                  onClick={() => setTypeFilter('ALL')}
                >
                  All Items
                </button>
                <button
                  className={`filter-pill ${typeFilter === 'VEG' ? 'active' : ''}`}
                  onClick={() => setTypeFilter('VEG')}
                >
                  🥬 Veg Only
                </button>
                <button
                  className={`filter-pill ${typeFilter === 'NON_VEG' ? 'active' : ''}`}
                  onClick={() => setTypeFilter('NON_VEG')}
                >
                  🍗 Non-Veg
                </button>

                {cuisinesList.filter((c) => c !== 'ALL').map((c) => (
                  <button
                    key={c}
                    className={`filter-pill ${selectedCuisine.toLowerCase() === c.toLowerCase() ? 'active' : ''}`}
                    onClick={() => setSelectedCuisine(selectedCuisine.toLowerCase() === c.toLowerCase() ? 'ALL' : c)}
                  >
                    {c}
                  </button>
                ))}
              </div>
            </div>

            {/* Food Grid */}
            {filteredFoodItems.length === 0 ? (
              <div className="cart-empty-state">
                <Utensils size={48} color="var(--text-dim)" style={{ margin: '0 auto 1rem' }} />
                <h3>No dishes found</h3>
                <p>Try searching for a different dish or switch your active restaurant above.</p>
              </div>
            ) : (
              <div className="food-grid">
                {filteredFoodItems.map((food) => {
                  const cartItem = cart.items?.find((i) => i.foodItemId === food.foodid)
                  const inCartQty = cartItem ? cartItem.quantity : 0

                  return (
                    <div key={food.foodid || food.foodname} className="food-card">
                      <div>
                        <div className="food-card-top">
                          <div className={`food-type-icon ${food.foodtype?.toUpperCase() === 'NON_VEG' ? 'non-veg' : 'veg'}`}>
                            <div className="dot"></div>
                          </div>
                          <span className="food-cuisine-tag">{food.cuisine || 'Special'}</span>
                        </div>

                        <h3 className="food-title">{food.foodname}</h3>
                        <p className="food-desc">{food.description}</p>
                      </div>

                      <div className="food-card-bottom">
                        <div className="food-price">₹{food.price?.toFixed(2)}</div>

                        {inCartQty > 0 ? (
                          <div className="qty-controls">
                            <button
                              className="qty-btn"
                              onClick={() => handleRemoveFromCart(cartItem.cartItemId)}
                            >
                              <Minus size={14} />
                            </button>
                            <span className="qty-val">{inCartQty}</span>
                            <button
                              className="qty-btn"
                              onClick={() => handleAddToCart(food)}
                            >
                              <Plus size={14} />
                            </button>
                          </div>
                        ) : (
                          <button
                            className="btn-add-food"
                            onClick={() => handleAddToCart(food)}
                          >
                            <Plus size={16} /> Add to Cart
                          </button>
                        )}
                      </div>
                    </div>
                  )
                })}
              </div>
            )}
          </div>
        )}

        {/* TAB 2: MY ORDERS & TRACKING */}
        {activeTab === 'orders' && (
          <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
              <div>
                <h1 style={{ fontSize: '1.8rem', fontWeight: '800' }}>Order History & Live Tracking</h1>
                <p style={{ color: 'var(--text-muted)' }}>Track status, review past meals, and check digital payment receipts.</p>
              </div>
              <button
                className="btn-secondary"
                onClick={loadInitialData}
                style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}
              >
                <RefreshCw size={16} className={loading ? 'spin' : ''} /> Refresh Orders
              </button>
            </div>

            {orders.length === 0 ? (
              <div className="cart-empty-state">
                <Clock size={48} color="var(--text-dim)" style={{ margin: '0 auto 1rem' }} />
                <h3>No orders placed yet</h3>
                <p>Browse the menu and place your first delicious food order!</p>
                <button className="btn-primary" style={{ marginTop: '1rem' }} onClick={() => setActiveTab('menu')}>
                  Browse Menu
                </button>
              </div>
            ) : (
              <div className="orders-list">
                {orders.map((order) => (
                  <div key={order.orderId || order.id} className="order-card">
                    <div className="order-card-header">
                      <div>
                        <div className="order-id-badge">Order #{order.orderId || order.id}</div>
                        <span style={{ fontSize: '0.85rem', color: 'var(--text-dim)' }}>
                          {order.createdAt ? new Date(order.createdAt).toLocaleString() : 'Just now'} • {order.paymentMethod || 'UPI'}
                        </span>
                      </div>

                      <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                        <span className={`order-status-pill ${order.orderStatus || 'PLACED'}`}>
                          {order.orderStatus || 'PLACED'}
                        </span>
                        <span style={{ fontSize: '1.25rem', fontWeight: '800', color: 'var(--text-main)' }}>
                          ₹{Number(order.totalAmount || 0).toFixed(2)}
                        </span>
                      </div>
                    </div>

                    {/* Step Visualizer */}
                    <div className="order-stepper">
                      <div className={`stepper-step ${['PLACED', 'CONFIRMED', 'PREPARING', 'OUT_FOR_DELIVERY', 'DELIVERED'].includes(order.orderStatus) ? 'completed' : ''}`}>
                        <div className="step-circle">1</div>
                        <span>Placed</span>
                      </div>
                      <div className={`stepper-step ${['CONFIRMED', 'PREPARING', 'OUT_FOR_DELIVERY', 'DELIVERED'].includes(order.orderStatus) ? 'completed' : ''}`}>
                        <div className="step-circle">2</div>
                        <span>Confirmed</span>
                      </div>
                      <div className={`stepper-step ${['PREPARING', 'OUT_FOR_DELIVERY', 'DELIVERED'].includes(order.orderStatus) ? 'completed' : ''}`}>
                        <div className="step-circle">3</div>
                        <span>Preparing</span>
                      </div>
                      <div className={`stepper-step ${['OUT_FOR_DELIVERY', 'DELIVERED'].includes(order.orderStatus) ? 'completed' : ''}`}>
                        <div className="step-circle">4</div>
                        <span>Out for Delivery</span>
                      </div>
                      <div className={`stepper-step ${order.orderStatus === 'DELIVERED' ? 'completed' : ''}`}>
                        <div className="step-circle">5</div>
                        <span>Delivered</span>
                      </div>
                    </div>

                    {/* Items List */}
                    <div className="order-items-box">
                      {order.items?.map((item, idx) => (
                        <div key={idx} className="order-item-line">
                          <span>
                            <strong>{item.quantity}x</strong> {item.itemName || 'Food Item'}
                          </span>
                          <span>₹{Number(item.totalPrice || 0).toFixed(2)}</span>
                        </div>
                      ))}
                    </div>

                    {/* Actions Bar */}
                    <div className="order-actions-bar">
                      <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                        <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>Simulate Status:</span>
                        <select
                          className="address-select"
                          style={{ width: 'auto', marginBottom: 0, padding: '0.4rem 0.8rem' }}
                          value={order.orderStatus || 'PLACED'}
                          onChange={async (e) => {
                            try {
                              await api.updateOrderStatus(order.orderId || order.id, e.target.value)
                              showToast(`Order status updated to ${e.target.value}!`, 'info')
                              loadInitialData()
                            } catch (err) {
                              showToast(err.message, 'error')
                            }
                          }}
                        >
                          <option value="PLACED">PLACED</option>
                          <option value="CONFIRMED">CONFIRMED</option>
                          <option value="PREPARING">PREPARING</option>
                          <option value="OUT_FOR_DELIVERY">OUT_FOR_DELIVERY</option>
                          <option value="DELIVERED">DELIVERED</option>
                          <option value="CANCELLED">CANCELLED</option>
                        </select>
                      </div>

                      <div style={{ display: 'flex', gap: '0.75rem' }}>
                        <button
                          className="btn-secondary"
                          onClick={() => {
                            setSelectedOrderForAction(order)
                            setShowReviewModal(true)
                          }}
                        >
                          <Star size={14} color="#fbbf24" /> Review Dish
                        </button>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {/* TAB 3: COMMUNITY REVIEWS */}
        {activeTab === 'reviews' && (
          <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
              <div>
                <h1 style={{ fontSize: '1.8rem', fontWeight: '800' }}>Customer Reviews & Ratings</h1>
                <p style={{ color: 'var(--text-muted)' }}>Real customer feedback on food quality, restaurant service, and delivery speed.</p>
              </div>
              <button className="btn-primary" onClick={() => setShowReviewModal(true)}>
                <Star size={16} /> Write a Review
              </button>
            </div>

            {feedbacks.length === 0 ? (
              <div className="cart-empty-state">
                <Star size={48} color="var(--text-dim)" style={{ margin: '0 auto 1rem' }} />
                <h3>No reviews submitted yet</h3>
                <p>Be the first customer to rate dishes from our restaurant partner!</p>
              </div>
            ) : (
              <div className="food-grid">
                {feedbacks.map((fb) => (
                  <div key={fb.id} className="food-card">
                    <div>
                      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.5rem' }}>
                        <div style={{ display: 'flex', gap: '0.2rem' }}>
                          {[...Array(5)].map((_, i) => (
                            <Star
                              key={i}
                              size={16}
                              color={i < fb.rating ? '#fbbf24' : '#475569'}
                              fill={i < fb.rating ? '#fbbf24' : 'transparent'}
                            />
                          ))}
                        </div>
                        <span style={{ fontSize: '0.8rem', color: 'var(--text-dim)' }}>
                          {fb.createdAt ? new Date(fb.createdAt).toLocaleDateString() : 'Recent'}
                        </span>
                      </div>

                      <p style={{ fontSize: '0.95rem', color: 'var(--text-main)', fontStyle: 'italic', marginBottom: '1rem' }}>
                        "{fb.comment}"
                      </p>
                    </div>

                    <div style={{ borderTop: '1px solid rgba(255,255,255,0.05)', paddingTop: '0.75rem', fontSize: '0.85rem', color: 'var(--text-muted)', display: 'flex', justifyContent: 'space-between' }}>
                      <span>👤 {fb.user?.name || 'Verified Foodie'}</span>
                      <span>🍔 {fb.foodItem?.foodname || fb.restaurant?.name || 'Restaurant'}</span>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {/* TAB 4: ADMIN HUB */}
        {activeTab === 'admin' && (
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(340px, 1fr))', gap: '2rem' }}>
            {/* 1. Add Restaurant */}
            <div className="admin-card">
              <div className="section-label">
                <Building size={16} color="var(--primary)" /> Partner Restaurant
              </div>
              <h2 style={{ fontSize: '1.25rem', marginBottom: '1rem' }}>Add New Restaurant</h2>

              <form onSubmit={handleAdminAddRestaurant}>
                <div className="form-field">
                  <label>Restaurant Name</label>
                  <input
                    type="text"
                    required
                    placeholder="e.g. Royal Punjab Kitchen"
                    value={adminRestForm.name}
                    onChange={(e) => setAdminRestForm({ ...adminRestForm, name: e.target.value })}
                  />
                </div>
                <div className="form-field">
                  <label>Contact Phone</label>
                  <input
                    type="text"
                    required
                    placeholder="e.g. 9876543210"
                    value={adminRestForm.phone}
                    onChange={(e) => setAdminRestForm({ ...adminRestForm, phone: e.target.value })}
                  />
                </div>
                <div className="form-field">
                  <label>Email Address</label>
                  <input
                    type="email"
                    required
                    placeholder="e.g. royalpunjab@gmail.com"
                    value={adminRestForm.email}
                    onChange={(e) => setAdminRestForm({ ...adminRestForm, email: e.target.value })}
                  />
                </div>
                <div className="form-field">
                  <label>Description</label>
                  <textarea
                    rows="2"
                    placeholder="Short restaurant description..."
                    value={adminRestForm.description}
                    onChange={(e) => setAdminRestForm({ ...adminRestForm, description: e.target.value })}
                  ></textarea>
                </div>
                <button type="submit" className="btn-primary" style={{ width: '100%' }}>
                  Create Restaurant
                </button>
              </form>
            </div>

            {/* 2. Add Food Item */}
            <div className="admin-card">
              <div className="section-label">
                <Utensils size={16} color="var(--secondary)" /> Menu Manager
              </div>
              <h2 style={{ fontSize: '1.25rem', marginBottom: '1rem' }}>Add Food Item</h2>

              <form onSubmit={handleAdminAddFoodItem}>
                <div className="form-field">
                  <label>Dish Name</label>
                  <input
                    type="text"
                    required
                    placeholder="e.g. Paneer Butter Masala"
                    value={adminFoodForm.foodname}
                    onChange={(e) => setAdminFoodForm({ ...adminFoodForm, foodname: e.target.value })}
                  />
                </div>
                <div className="form-grid-2">
                  <div className="form-field">
                    <label>Type</label>
                    <select
                      value={adminFoodForm.foodtype}
                      onChange={(e) => setAdminFoodForm({ ...adminFoodForm, foodtype: e.target.value })}
                    >
                      <option value="VEG">VEG</option>
                      <option value="NON_VEG">NON_VEG</option>
                    </select>
                  </div>
                  <div className="form-field">
                    <label>Price (₹)</label>
                    <input
                      type="number"
                      required
                      min="10"
                      value={adminFoodForm.price}
                      onChange={(e) => setAdminFoodForm({ ...adminFoodForm, price: e.target.value })}
                    />
                  </div>
                </div>
                <div className="form-field">
                  <label>Cuisine Category</label>
                  <input
                    type="text"
                    required
                    placeholder="e.g. North Indian, Italian, Chinese"
                    value={adminFoodForm.cuisine}
                    onChange={(e) => setAdminFoodForm({ ...adminFoodForm, cuisine: e.target.value })}
                  />
                </div>
                <div className="form-field">
                  <label>Description</label>
                  <textarea
                    rows="2"
                    placeholder="Ingredients, spice level, preparation style..."
                    value={adminFoodForm.description}
                    onChange={(e) => setAdminFoodForm({ ...adminFoodForm, description: e.target.value })}
                  ></textarea>
                </div>
                <button type="submit" className="btn-primary" style={{ width: '100%' }}>
                  Add Dish to Menu
                </button>
              </form>
            </div>

            {/* 3. Delivery Pricing Rule Config */}
            <div className="admin-card">
              <div className="section-label">
                <DollarSign size={16} color="var(--accent)" /> Distance & Pricing Rules
              </div>
              <h2 style={{ fontSize: '1.25rem', marginBottom: '1rem' }}>Delivery Fee Rules</h2>

              <form onSubmit={handleAdminSavePricing}>
                <div className="form-grid-2">
                  <div className="form-field">
                    <label>Base Delivery Fee (₹)</label>
                    <input
                      type="number"
                      required
                      min="10"
                      value={adminPricingForm.basefees}
                      onChange={(e) => setAdminPricingForm({ ...adminPricingForm, basefees: e.target.value })}
                    />
                  </div>
                  <div className="form-field">
                    <label>Rate Per Km (₹)</label>
                    <input
                      type="number"
                      required
                      min="1"
                      value={adminPricingForm.perKmRate}
                      onChange={(e) => setAdminPricingForm({ ...adminPricingForm, perKmRate: e.target.value })}
                    />
                  </div>
                </div>

                <div className="form-grid-2">
                  <div className="form-field">
                    <label>Max Delivery Radius (Km)</label>
                    <input
                      type="number"
                      required
                      min="1"
                      value={adminPricingForm.maxdelieveryradius}
                      onChange={(e) => setAdminPricingForm({ ...adminPricingForm, maxdelieveryradius: e.target.value })}
                    />
                  </div>
                  <div className="form-field">
                    <label>Free Delivery Above (₹)</label>
                    <input
                      type="number"
                      required
                      min="100"
                      value={adminPricingForm.freeDelievery}
                      onChange={(e) => setAdminPricingForm({ ...adminPricingForm, freeDelievery: e.target.value })}
                    />
                  </div>
                </div>

                <button type="submit" className="btn-primary" style={{ width: '100%', marginTop: '0.5rem' }}>
                  Update Pricing Configuration
                </button>
              </form>
            </div>

            {/* 4. Live Payments & Gateway Ledger */}
            <div className="admin-card" style={{ gridColumn: '1 / -1' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
                <div>
                  <div className="section-label">
                    <CreditCard size={16} color="var(--primary)" /> Stripe Gateway & Payments Ledger
                  </div>
                  <h2 style={{ fontSize: '1.25rem' }}>Live Transactions (Stripe Mock & COD)</h2>
                  <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>
                    Monitors live payment intents created with Stripe Mock Server at <code>http://194.242.57.93:12111</code>.
                  </p>
                </div>
                <button
                  className="btn-secondary"
                  onClick={async () => {
                    const payData = await api.getAllPayments().catch(() => [])
                    setPayments(Array.isArray(payData) ? payData : [])
                    showToast('Payments ledger refreshed!', 'info')
                  }}
                  style={{ display: 'flex', alignItems: 'center', gap: '0.4rem', padding: '0.4rem 0.8rem', fontSize: '0.85rem' }}
                >
                  <RefreshCw size={14} /> Refresh Ledger
                </button>
              </div>

              {payments.length === 0 ? (
                <div style={{ textAlign: 'center', padding: '2rem', color: 'var(--text-dim)' }}>
                  No payment records found yet. Place an order to initiate a Stripe mock payment intent!
                </div>
              ) : (
                <div style={{ overflowX: 'auto' }}>
                  <table className="payments-table">
                    <thead>
                      <tr>
                        <th>Payment ID</th>
                        <th>Transaction / Intent ID</th>
                        <th>Order #</th>
                        <th>User ID</th>
                        <th>Amount</th>
                        <th>Method</th>
                        <th>Status</th>
                        <th>Timestamp</th>
                        <th>Actions</th>
                      </tr>
                    </thead>
                    <tbody>
                      {payments.map((p) => (
                        <tr key={p.id || p.transactionId}>
                          <td><strong>#{p.id}</strong></td>
                          <td>
                            <code style={{ color: '#38bdf8', background: 'rgba(255,255,255,0.05)', padding: '0.15rem 0.4rem', borderRadius: '4px' }}>
                              {p.transactionId || 'N/A'}
                            </code>
                          </td>
                          <td>Order #{p.orderId}</td>
                          <td>User #{p.userId}</td>
                          <td style={{ fontWeight: '700' }}>₹{Number(p.amount || 0).toFixed(2)}</td>
                          <td>
                            <span style={{ fontSize: '0.8rem', color: p.paymentMethod === 'STRIPE' ? '#818cf8' : '#cbd5e1' }}>
                              {p.paymentMethod === 'STRIPE' ? '💳 Stripe Mock' : p.paymentMethod}
                            </span>
                          </td>
                          <td>
                            <span className={
                              ['PAID', 'SUCCESS', 'SUCCEEDED'].includes((p.paymentStatus || '').toUpperCase())
                                ? 'badge-paid'
                                : (p.paymentStatus || '').toUpperCase() === 'FAILED'
                                ? 'badge-failed'
                                : 'badge-pending'
                            }>
                              {p.paymentStatus || 'PENDING'}
                            </span>
                          </td>
                          <td style={{ fontSize: '0.8rem', color: 'var(--text-dim)' }}>
                            {p.paymentDate ? new Date(p.paymentDate).toLocaleString() : 'Just now'}
                          </td>
                          <td>
                            <select
                              className="address-select"
                              style={{ width: 'auto', marginBottom: 0, padding: '0.25rem 0.5rem', fontSize: '0.75rem' }}
                              value={p.paymentStatus || 'PENDING'}
                              onChange={async (e) => {
                                try {
                                  await api.updatePaymentStatus(p.id, e.target.value)
                                  showToast(`Payment #${p.id} marked as ${e.target.value}`, 'success')
                                  const refreshed = await api.getAllPayments()
                                  setPayments(refreshed)
                                } catch (err) {
                                  showToast(err.message, 'error')
                                }
                              }}
                            >
                              <option value="PAID">PAID</option>
                              <option value="SUCCESS">SUCCESS</option>
                              <option value="PENDING">PENDING</option>
                              <option value="REQUIRES_PAYMENT_METHOD">REQ_METHOD</option>
                              <option value="REFUNDED">REFUNDED</option>
                              <option value="FAILED">FAILED</option>
                            </select>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              )}
            </div>
          </div>
        )}
      </main>

      {/* ========================================================================
          SLIDE-OVER CART DRAWER
          ======================================================================== */}
      {cartOpen && (
        <>
          <div className="drawer-backdrop" onClick={() => setCartOpen(false)}></div>
          <div className="cart-drawer">
            <div className="drawer-header">
              <h2>
                <ShoppingBag size={22} color="var(--primary)" /> Your Cart
              </h2>
              <button className="btn-close" onClick={() => setCartOpen(false)}>
                <X size={20} />
              </button>
            </div>

            <div className="drawer-body">
              {cart.items.length === 0 ? (
                <div className="cart-empty-state">
                  <div className="icon-empty">🛒</div>
                  <h3>Your cart is empty</h3>
                  <p>Explore our menu and add some mouth-watering dishes!</p>
                </div>
              ) : (
                <>
                  {/* Items List */}
                  <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
                    {cart.items.map((item) => (
                      <div key={item.cartItemId || item.foodItemId} className="cart-item-row">
                        <div className="cart-item-info">
                          <h4>{item.foodname}</h4>
                          <p>₹{item.unitPrice?.toFixed(2)} each</p>
                        </div>

                        <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                          <span style={{ fontWeight: '800', color: 'var(--text-main)' }}>
                            ₹{item.subtotal?.toFixed(2)}
                          </span>
                          <button
                            className="btn-icon"
                            style={{ background: 'rgba(239,68,68,0.15)', color: 'var(--danger)', border: 'none', width: '30px', height: '30px', borderRadius: '50%', cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'center' }}
                            onClick={() => handleRemoveFromCart(item.cartItemId)}
                            title="Remove"
                          >
                            <Trash2 size={14} />
                          </button>
                        </div>
                      </div>
                    ))}
                  </div>

                  {/* Delivery Address Box */}
                  <div className="checkout-section-card">
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.5rem' }}>
                      <span className="section-label" style={{ marginBottom: 0 }}>
                        <MapPin size={14} color="var(--primary)" /> Delivery Address
                      </span>
                      <button
                        style={{ background: 'transparent', border: 'none', color: 'var(--primary)', fontSize: '0.8rem', fontWeight: '700', cursor: 'pointer' }}
                        onClick={() => setShowAddressModal(true)}
                      >
                        + Add New
                      </button>
                    </div>

                    {currentUser?.addresses && currentUser.addresses.length > 0 ? (
                      <select
                        className="address-select"
                        value={selectedAddressId || ''}
                        onChange={(e) => setSelectedAddressId(Number(e.target.value))}
                      >
                        {currentUser.addresses.map((addr) => (
                          <option key={addr.addressId || addr.id} value={addr.addressId || addr.id}>
                            {addr.addressType || 'HOME'}: {addr.houseNo}, {addr.street}, {addr.city} ({addr.pincode})
                          </option>
                        ))}
                      </select>
                    ) : (
                      <p style={{ fontSize: '0.85rem', color: 'var(--danger)', marginBottom: '0.5rem' }}>
                        No address found. Click "+ Add New" above.
                      </p>
                    )}

                    <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', fontSize: '0.8rem', color: 'var(--text-dim)' }}>
                      <Truck size={14} /> Estimated Distance: ~{deliveryInfo.distance} km
                    </div>
                  </div>

                  {/* Payment Method Selector */}
                  <div className="checkout-section-card">
                    <span className="section-label">
                      <CreditCard size={14} color="var(--secondary)" /> Payment Method
                    </span>
                    <div className="payment-options">
                      {['UPI', 'CARD', 'COD'].map((method) => (
                        <div
                          key={method}
                          className={`payment-option-btn ${paymentMethod === method ? 'selected' : ''}`}
                          onClick={() => setPaymentMethod(method)}
                        >
                          {method === 'UPI' && '📱 UPI / QR'}
                          {method === 'CARD' && '💳 Card'}
                          {method === 'COD' && '💵 Cash on Del.'}
                        </div>
                      ))}
                    </div>
                  </div>

                  {/* Bill Breakdown */}
                  <div className="checkout-section-card">
                    <span className="section-label">Bill Breakdown</span>
                    <div className="bill-row">
                      <span>Item Subtotal</span>
                      <span>₹{(cart.totalAmount || 0).toFixed(2)}</span>
                    </div>
                    <div className="bill-row">
                      <span>Delivery Fee ({deliveryInfo.distance} km)</span>
                      <span>{deliveryInfo.deliveryFee === 0 ? 'FREE' : `₹${deliveryInfo.deliveryFee.toFixed(2)}`}</span>
                    </div>
                    <div className="bill-row">
                      <span>GST & Restaurant Taxes (0%)</span>
                      <span>₹0.00</span>
                    </div>
                    <div className="bill-row total">
                      <span>Total Amount</span>
                      <span style={{ color: 'var(--primary)' }}>₹{grandTotal.toFixed(2)}</span>
                    </div>
                  </div>
                </>
              )}
            </div>

            {cart.items.length > 0 && (
              <div className="drawer-footer">
                <button
                  className="btn-place-order"
                  disabled={loading || !selectedAddressId}
                  onClick={handlePlaceOrder}
                >
                  {loading ? (
                    <>
                      <RefreshCw size={18} className="spin" /> Processing Order...
                    </>
                  ) : (
                    <>
                      Place Order • ₹{grandTotal.toFixed(2)} <ArrowRight size={18} />
                    </>
                  )}
                </button>
              </div>
            )}
          </div>
        </>
      )}

      {/* ========================================================================
          MODAL 1: REGISTER CUSTOMER & ADDRESS
          ======================================================================== */}
      {showUserModal && (
        <div className="modal-backdrop" onClick={() => setShowUserModal(false)}>
          <div className="modal-box" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2 style={{ fontSize: '1.35rem', fontWeight: '800' }}>Register Customer</h2>
              <button className="btn-close" onClick={() => setShowUserModal(false)}>
                <X size={18} />
              </button>
            </div>

            <form onSubmit={handleRegisterUser}>
              <div className="form-field">
                <label>Full Name</label>
                <input
                  type="text"
                  required
                  placeholder="e.g. Rahul Sharma"
                  value={userForm.name}
                  onChange={(e) => setUserForm({ ...userForm, name: e.target.value })}
                />
              </div>

              <div className="form-grid-2">
                <div className="form-field">
                  <label>Email Address</label>
                  <input
                    type="email"
                    required
                    placeholder="e.g. rahul.sharma@gmail.com"
                    value={userForm.email}
                    onChange={(e) => setUserForm({ ...userForm, email: e.target.value })}
                  />
                </div>
                <div className="form-field">
                  <label>Mobile Number (10 digits)</label>
                  <input
                    type="tel"
                    required
                    maxLength="10"
                    placeholder="9876543210"
                    value={userForm.mobile}
                    onChange={(e) => setUserForm({ ...userForm, mobile: e.target.value })}
                  />
                </div>
              </div>

              <div className="section-label" style={{ marginTop: '1rem' }}>
                <MapPin size={14} color="var(--primary)" /> Initial Delivery Address
              </div>

              <div className="form-grid-2">
                <div className="form-field">
                  <label>Flat / House No</label>
                  <input
                    type="text"
                    required
                    value={userForm.houseNo}
                    onChange={(e) => setUserForm({ ...userForm, houseNo: e.target.value })}
                  />
                </div>
                <div className="form-field">
                  <label>Building / Society</label>
                  <input
                    type="text"
                    required
                    value={userForm.buildingName}
                    onChange={(e) => setUserForm({ ...userForm, buildingName: e.target.value })}
                  />
                </div>
              </div>

              <div className="form-grid-2">
                <div className="form-field">
                  <label>Street / Area</label>
                  <input
                    type="text"
                    required
                    value={userForm.street}
                    onChange={(e) => setUserForm({ ...userForm, street: e.target.value })}
                  />
                </div>
                <div className="form-field">
                  <label>City & Pincode</label>
                  <div style={{ display: 'flex', gap: '0.5rem' }}>
                    <input
                      type="text"
                      required
                      value={userForm.city}
                      onChange={(e) => setUserForm({ ...userForm, city: e.target.value })}
                    />
                    <input
                      type="number"
                      required
                      value={userForm.pincode}
                      onChange={(e) => setUserForm({ ...userForm, pincode: e.target.value })}
                    />
                  </div>
                </div>
              </div>

              <button type="submit" className="btn-primary" style={{ width: '100%', marginTop: '1rem' }}>
                Create Account & Sign In
              </button>
            </form>
          </div>
        </div>
      )}

      {/* ========================================================================
          MODAL 2: ADD DELIVERY ADDRESS
          ======================================================================== */}
      {showAddressModal && (
        <div className="modal-backdrop" onClick={() => setShowAddressModal(false)}>
          <div className="modal-box" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2 style={{ fontSize: '1.35rem', fontWeight: '800' }}>Add Delivery Address</h2>
              <button className="btn-close" onClick={() => setShowAddressModal(false)}>
                <X size={18} />
              </button>
            </div>

            <form onSubmit={handleAddAddress}>
              <div className="form-grid-2">
                <div className="form-field">
                  <label>Flat / House No</label>
                  <input
                    type="text"
                    required
                    placeholder="Flat 402"
                    value={addressForm.houseNo}
                    onChange={(e) => setAddressForm({ ...addressForm, houseNo: e.target.value })}
                  />
                </div>
                <div className="form-field">
                  <label>Building Name</label>
                  <input
                    type="text"
                    required
                    placeholder="Shree Heights"
                    value={addressForm.buildingName}
                    onChange={(e) => setAddressForm({ ...addressForm, buildingName: e.target.value })}
                  />
                </div>
              </div>

              <div className="form-field">
                <label>Street Address & Landmark</label>
                <input
                  type="text"
                  required
                  placeholder="MG Road, near Metro station"
                  value={addressForm.street}
                  onChange={(e) => setAddressForm({ ...addressForm, street: e.target.value })}
                />
              </div>

              <div className="form-grid-2">
                <div className="form-field">
                  <label>City</label>
                  <input
                    type="text"
                    required
                    value={addressForm.city}
                    onChange={(e) => setAddressForm({ ...addressForm, city: e.target.value })}
                  />
                </div>
                <div className="form-field">
                  <label>Pincode</label>
                  <input
                    type="number"
                    required
                    value={addressForm.pincode}
                    onChange={(e) => setAddressForm({ ...addressForm, pincode: e.target.value })}
                  />
                </div>
              </div>

              <div className="form-field">
                <label>Address Type</label>
                <select
                  value={addressForm.addressType}
                  onChange={(e) => setAddressForm({ ...addressForm, addressType: e.target.value })}
                >
                  <option value="HOME">HOME</option>
                  <option value="WORK">WORK</option>
                  <option value="OTHER">OTHER</option>
                </select>
              </div>

              <button type="submit" className="btn-primary" style={{ width: '100%', marginTop: '1rem' }}>
                Save Delivery Address
              </button>
            </form>
          </div>
        </div>
      )}

      {/* ========================================================================
          MODAL 3: FEEDBACK / REVIEW
          ======================================================================== */}
      {showReviewModal && (
        <div className="modal-backdrop" onClick={() => setShowReviewModal(false)}>
          <div className="modal-box" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2 style={{ fontSize: '1.35rem', fontWeight: '800' }}>Rate & Review</h2>
              <button className="btn-close" onClick={() => setShowReviewModal(false)}>
                <X size={18} />
              </button>
            </div>

            <form onSubmit={handleSubmitReview}>
              <div style={{ textAlign: 'center', margin: '1.5rem 0' }}>
                <div className="star-rating" style={{ justifyContent: 'center' }}>
                  {[1, 2, 3, 4, 5].map((star) => (
                    <span
                      key={star}
                      className={`star-icon ${star <= reviewForm.rating ? 'active' : ''}`}
                      onClick={() => setReviewForm({ ...reviewForm, rating: star })}
                    >
                      ★
                    </span>
                  ))}
                </div>
                <span style={{ fontSize: '0.9rem', color: 'var(--text-muted)', marginTop: '0.5rem', display: 'block' }}>
                  {reviewForm.rating === 5 && '🌟 Outstanding! Loved it!'}
                  {reviewForm.rating === 4 && '👍 Great experience'}
                  {reviewForm.rating === 3 && '👌 Good food'}
                  {reviewForm.rating <= 2 && '👎 Needs improvement'}
                </span>
              </div>

              <div className="form-field">
                <label>Feedback & Comments</label>
                <textarea
                  rows="3"
                  required
                  placeholder="Tell us what you loved about the food, taste, packaging..."
                  value={reviewForm.comment}
                  onChange={(e) => setReviewForm({ ...reviewForm, comment: e.target.value })}
                ></textarea>
              </div>

              <button type="submit" className="btn-primary" style={{ width: '100%' }}>
                Submit Review
              </button>
            </form>
          </div>
        </div>
      )}

      {/* ========================================================================
          MODAL 4: STRIPE MOCK PAYMENT GATEWAY CHECKOUT
          ======================================================================== */}
      {showPaymentModal && stripePaymentData && (
        <div className="modal-backdrop" onClick={() => setShowPaymentModal(false)}>
          <div className="modal-box" style={{ maxWidth: '580px' }} onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.6rem' }}>
                <div style={{ background: 'linear-gradient(135deg, #6366f1, #8b5cf6)', padding: '0.4rem', borderRadius: '8px', color: '#fff', display: 'flex' }}>
                  <CreditCard size={20} />
                </div>
                <div>
                  <h2 style={{ fontSize: '1.3rem', fontWeight: '800' }}>Stripe Mock Checkout</h2>
                  <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Secure Sandbox Gateway</span>
                </div>
              </div>
              <button className="btn-close" onClick={() => setShowPaymentModal(false)}>
                <X size={18} />
              </button>
            </div>

            {/* Gateway Banner */}
            <div className="stripe-banner">
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <ShieldCheck size={20} color="#818cf8" />
                <div>
                  <div style={{ fontSize: '0.85rem', fontWeight: '700' }}>Stripe-Mock Server Connected</div>
                  <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Target: http://194.242.57.93:12111</div>
                </div>
              </div>
              <span className="stripe-badge">● TEST MODE</span>
            </div>

            {/* Mock Credit Card Visual */}
            <div className="mock-card-visual">
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div className="mock-card-chip"></div>
                <div style={{ fontWeight: '800', fontStyle: 'italic', letterSpacing: '1px', fontSize: '1.1rem' }}>
                  STRIPE
                </div>
              </div>

              <div className="mock-card-number">
                4242 &bull;&bull;&bull;&bull; &bull;&bull;&bull;&bull; 4242
              </div>

              <div className="mock-card-footer">
                <div>
                  <div style={{ fontSize: '0.65rem', opacity: 0.7 }}>CARDHOLDER</div>
                  <div className="name">{currentUser?.name || 'Rahul Sharma'}</div>
                </div>
                <div>
                  <div style={{ fontSize: '0.65rem', opacity: 0.7 }}>EXPIRES</div>
                  <div>12/28</div>
                </div>
                <div>
                  <div style={{ fontSize: '0.65rem', opacity: 0.7 }}>CVC</div>
                  <div>888</div>
                </div>
              </div>
            </div>

            {/* Transaction & Intent Details */}
            <div className="stripe-meta-box">
              <div className="stripe-meta-row">
                <span>Payment Intent ID:</span>
                <code>{stripePaymentData.transactionId || 'pi_mock_...'}</code>
              </div>
              <div className="stripe-meta-row">
                <span>Client Secret:</span>
                <code>{stripePaymentData.clientSecret || 'pi_..._secret_...'}</code>
              </div>
              <div className="stripe-meta-row">
                <span>Order Reference:</span>
                <span style={{ fontWeight: '700', color: 'var(--text-main)' }}>Order #{stripePaymentData.orderId}</span>
              </div>
              <div className="stripe-meta-row" style={{ borderTop: '1px solid rgba(255,255,255,0.06)', paddingTop: '0.4rem', marginTop: '0.2rem' }}>
                <span style={{ fontSize: '0.9rem', fontWeight: '700' }}>Total Charge:</span>
                <span style={{ fontSize: '1.1rem', fontWeight: '800', color: 'var(--primary)' }}>
                  ₹{Number(stripePaymentData.amount || 0).toFixed(2)}
                </span>
              </div>
            </div>

            {/* Pay Button */}
            <button
              className="btn-stripe-pay"
              disabled={loading}
              onClick={handleAuthorizeStripePayment}
            >
              {loading ? (
                <>
                  <RefreshCw size={18} className="spin" /> Authorizing via Stripe Mock...
                </>
              ) : (
                <>
                  <ShieldCheck size={18} /> Authorize & Pay ₹{Number(stripePaymentData.amount || 0).toFixed(2)}
                </>
              )}
            </button>

            <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', gap: '0.4rem', marginTop: '0.75rem', fontSize: '0.75rem', color: 'var(--text-dim)' }}>
              <ShieldCheck size={12} /> TLS 256-bit Mock Encryption • Immediate Settlement
            </div>
          </div>
        </div>
      )}
      <footer className="footer">
        FoodDelivery Enterprise Platform • Built with Spring Boot 3 & React • CI/CD Verified
      </footer>
    </div>
  )
}
